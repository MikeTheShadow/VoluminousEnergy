#!/usr/bin/env python3
"""Runs the 26.3 port test plan in throwaway worlds.

The plan is openspec/changes/port-to-26-3/test-plan.md. Every world, config file, and log this
script creates lives under the directory given with --dir. It never writes to runs/server,
runs/client, or the worlds in them: servers and clients run with their working directory moved
under --dir by a Gradle init script.

Usage: autotest.py --dir DIR {snapshot,verify,t0,t1,t2,t3,t4,t5,report,all}
"""
import argparse
import hashlib
import json
import os
import re
import secrets
import shutil
import socket
import struct
import subprocess
import sys
import time
from pathlib import Path

REPO = Path(__file__).resolve().parents[3]
CHANGE = REPO / "openspec" / "changes" / "port-to-26-3"
MODID = "voluminousenergy"
SEED = "-4172144997902289642"
DATA_PACK_FORMAT = 121
STARTUP_CONFIG = Path("config") / MODID / f"{MODID}-startup.toml"
RUNNING = []


# Results ----------------------------------------------------------------------------------------

class Results:
    def __init__(self, directory: Path):
        self.path = directory / "results.json"
        self.entries = json.loads(self.path.read_text()) if self.path.exists() else {}

    def record(self, test_id, status, detail=""):
        self.entries[test_id] = {"status": status, "detail": str(detail)}
        self.path.write_text(json.dumps(self.entries, indent=2))
        print(f"[{status.upper():4}] {test_id} {detail}".rstrip(), flush=True)

    def failed(self):
        return [key for key, value in self.entries.items() if value["status"] == "fail"]


def check(results, test_id, condition, detail=""):
    results.record(test_id, "pass" if condition else "fail", detail)
    return condition


# Safety -----------------------------------------------------------------------------------------

def protected_files():
    files = [REPO / "runs" / "server" / "server.properties"]
    files += sorted((REPO / "runs" / "server").glob("world*/level.dat"))
    files += sorted((REPO / "runs" / "client" / "saves").glob("*/level.dat"))
    return [path for path in files if path.exists()]


def fingerprint_file(path: Path):
    return {"sha256": hashlib.sha256(path.read_bytes()).hexdigest(), "mtime": path.stat().st_mtime_ns}


def snapshot(directory: Path):
    data = {str(path): fingerprint_file(path) for path in protected_files()}
    (directory / "protected.json").write_text(json.dumps(data, indent=2))
    print(f"snapshot of {len(data)} protected files", flush=True)


def verify(directory: Path, results: Results):
    saved = json.loads((directory / "protected.json").read_text())
    changed = [path for path, value in saved.items() if not Path(path).exists() or fingerprint_file(Path(path)) != value]
    added = [str(path) for path in protected_files() if str(path) not in saved]
    check(results, "11.6", not changed and not added,
          f"{len(saved)} files unchanged" if not changed and not added else f"changed {changed} added {added}")


# Processes --------------------------------------------------------------------------------------

def gradle(args, log: Path, extra_env=None, timeout=3600):
    env = dict(os.environ, **(extra_env or {}))
    with open(log, "w") as out:
        process = subprocess.run(["./gradlew", *args], cwd=REPO, stdout=out, stderr=subprocess.STDOUT, env=env, timeout=timeout)
    return process.returncode


def free_port():
    with socket.socket() as probe:
        probe.bind(("127.0.0.1", 0))
        return probe.getsockname()[1]


def write_init_script(path: Path, run_name, working_directory: Path, system_properties, arguments, environment=None):
    properties = "\n".join(f"                run.systemProperties.put('{key}', '{value}')" for key, value in system_properties.items())
    properties += "".join(f"\n                run.environmentVariables.put('{key}', '{value}')" for key, value in (environment or {}).items())
    argument_list = ", ".join(f"'{argument}'" for argument in arguments)
    path.write_text(f"""// Written by autotest.py: moves the {run_name} run into a throwaway directory.
allprojects {{
    plugins.withId('net.neoforged.gradle.userdev') {{
        extensions.getByName('runs').configureEach {{ run ->
            if (run.name == '{run_name}') {{
                run.workingDirectory.set(new File('{working_directory}'))
{properties}
                run.arguments.addAll([{argument_list}])
            }}
        }}
    }}
}}
""")


class Rcon:
    def __init__(self, port, password):
        self.socket = socket.create_connection(("127.0.0.1", port), timeout=600)
        self.request = 0
        self._send(3, password)
        if self._receive()[0] == -1:
            raise RuntimeError("RCON login refused")

    def _send(self, kind, payload):
        self.request += 1
        body = struct.pack("<ii", self.request, kind) + payload.encode("utf-8") + b"\x00\x00"
        self.socket.sendall(struct.pack("<i", len(body)) + body)

    def _receive(self):
        length = struct.unpack("<i", self._read(4))[0]
        body = self._read(length)
        request, _ = struct.unpack("<ii", body[:8])
        return request, body[8:-2].decode("utf-8", "replace")

    def _read(self, count):
        data = b""
        while len(data) < count:
            chunk = self.socket.recv(count - len(data))
            if not chunk:
                raise ConnectionError("RCON closed")
            data += chunk
        return data

    def command(self, command):
        """Runs a command; Minecraft splits long responses, so a second request marks the end.

        The server reads one packet per socket read and disconnects when two arrive together, so the
        marker goes out only once the command has answered."""
        self._send(2, command)
        command_id = self.request
        request, body = self._receive()
        parts = [body] if request == command_id else []
        self._send(0, "")
        end_id = self.request
        while True:
            request, body = self._receive()
            if request == end_id:
                return "".join(parts)
            if request == command_id:
                parts.append(body)

    def close(self):
        self.socket.close()


class Server:
    """A dedicated server of this checkout (or of a worktree) in its own throwaway directory."""

    def __init__(self, directory: Path, name, repo=REPO, seed=SEED, radius=20, startup_edits=None, datapacks=None, properties=None):
        self.directory = directory / "server" / name
        self.repo = repo
        self.name = name
        self.seed = seed
        self.radius = radius
        self.startup_edits = startup_edits or {}
        self.datapacks = datapacks or {}
        self.extra_properties = properties or {}
        self.process = None
        self.rcon = None
        self.log = None

    def prepare(self, startup_config_template=None, keep_world=False):
        if not keep_world:
            shutil.rmtree(self.directory / "world", ignore_errors=True)
        self.directory.mkdir(parents=True, exist_ok=True)
        shutil.copy(REPO / "runs" / "server" / "eula.txt", self.directory / "eula.txt")
        self.rcon_port = free_port()
        self.password = secrets.token_hex(16)
        enabled = ["vanilla", "mod_data"] + [f"file/{pack}" for pack in self.datapacks]
        properties = {
            "level-name": "world", "level-seed": self.seed, "online-mode": "false", "server-ip": "127.0.0.1",
            "server-port": str(free_port()), "enable-rcon": "true", "rcon.port": str(self.rcon_port),
            "rcon.password": self.password, "spawn-protection": "0", "max-tick-time": "-1",
            "initial-enabled-packs": ",".join(enabled), "view-distance": "4", "simulation-distance": "4",
            "pause-when-empty-seconds": "0",
        }
        properties.update(self.extra_properties)
        (self.directory / "server.properties").write_text("".join(f"{key}={value}\n" for key, value in properties.items()))
        for pack, files in self.datapacks.items():
            root = self.directory / "world" / "datapacks" / pack
            write_json(root / "pack.mcmeta", {"pack": {"description": f"autotest {pack}", "min_format": DATA_PACK_FORMAT, "max_format": [DATA_PACK_FORMAT, 0]}})
            for relative, content in files.items():
                write_json(root / relative, content)
        if startup_config_template is not None and self.startup_edits:
            text = startup_config_template.read_text()
            for key, value in self.startup_edits.items():
                text, count = re.subn(rf'("{re.escape(key)}"\s*=\s*)\S+', rf"\g<1>{value}", text)
                if count != 1:
                    raise RuntimeError(f"config key {key} not found")
            target = self.directory / STARTUP_CONFIG
            target.parent.mkdir(parents=True, exist_ok=True)
            target.write_text(text)

    def start(self, timeout=1200):
        init = self.directory / "autotest-init.gradle"
        write_init_script(init, "server", self.directory, {"ve.autotest.radius": self.radius}, [])
        self.log = self.directory / f"console-{int(time.time())}.log"
        out = open(self.log, "w")
        self.process = subprocess.Popen(["./gradlew", "-I", str(init), "--no-configuration-cache", "runServer"],
                                        cwd=self.repo, stdout=out, stderr=subprocess.STDOUT)
        RUNNING.append(self)
        deadline = time.time() + timeout
        while time.time() < deadline:
            if self.process.poll() is not None:
                return False
            if re.search(r"Done \([0-9.]+s\)!", self.log.read_text(errors="replace")):
                self.rcon = Rcon(self.rcon_port, self.password)
                return True
            time.sleep(2)
        self.stop()
        return False

    def run_test(self, test, timeout=3600):
        """Runs one gametest away from the survey area and returns (passed, detail)."""
        offset = len(self.log.read_text(errors="replace"))
        command = f"execute in minecraft:overworld positioned 100000 100 100000 run test run {MODID}:{test}"
        reply = self.rcon.command(command)
        if "Running" not in reply:
            time.sleep(5)
            reply = self.rcon.command(command)
        if "Running" not in reply:
            return False, f"test did not start: {reply!r}"
        deadline = time.time() + timeout
        while time.time() < deadline:
            text = self.log.read_text(errors="replace")[offset:]
            passed = re.search(rf"VE-AUTOTEST PASS {re.escape(test)}\b(.*)", text)
            failed = re.search(rf"{MODID}:{re.escape(test)} failed at [^!]*! (.*)", text)
            if failed:
                return False, failed.group(1).strip()
            if passed:
                return True, passed.group(1).strip()
            if self.process.poll() is not None:
                return False, "server stopped"
            time.sleep(1)
        return False, "timed out"

    def values(self, test):
        text = self.log.read_text(errors="replace")
        return dict(re.findall(rf"VE-AUTOTEST VALUE {re.escape(test)} (\S+)=(.*)", text))

    def stop(self, timeout=600):
        if self.rcon is not None:
            try:
                self.rcon.command("stop")
            except OSError:
                pass
            self.rcon.close()
            self.rcon = None
        if self.process is not None:
            try:
                self.process.wait(timeout=timeout)
            except subprocess.TimeoutExpired:
                self.process.kill()
                self.process.wait()
        self._end_strays()
        if self in RUNNING:
            RUNNING.remove(self)
        if self.log is not None:
            with open(self.log, "a") as out:
                out.write("\n")

    def _end_strays(self):
        """Ends the game process the Gradle daemon forked, which outlives a killed gradlew client."""
        strays = []
        for entry in Path("/proc").iterdir():
            try:
                if entry.name.isdigit() and Path(os.readlink(entry / "cwd")) == self.directory:
                    strays.append(int(entry.name))
            except OSError:
                continue
        for pid in strays:
            try:
                os.kill(pid, 15)
            except OSError:
                continue
        deadline = time.time() + 60
        while strays and time.time() < deadline:
            strays = [pid for pid in strays if Path(f"/proc/{pid}").exists()]
            time.sleep(1)
        for pid in strays:
            try:
                os.kill(pid, 9)
            except OSError:
                pass


def write_json(path: Path, content):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(content, indent=2))


# Log scan ---------------------------------------------------------------------------------------

LOG_PROBLEMS = [
    re.compile(r"(ERROR|FATAL).*" + MODID, re.IGNORECASE),
    re.compile(r"Exception.*" + MODID),
    re.compile(r"(Missing|Unable to load) (model|texture).*" + MODID),
    re.compile(r"Registry loading errors|Failed to load datapacks|Couldn't parse data file"),
    re.compile(r"Attempted to load class .* for invalid dist"),
]
# Lines the gametest framework logs for a deliberate failure, which the results already record.
LOG_EXPECTED = [re.compile(r"LogTestReporter")]


def scan_log(path: Path, ignore=()):
    problems = []
    for line in path.read_text(errors="replace").splitlines():
        if any(pattern.search(line) for pattern in LOG_EXPECTED) or any(re.search(pattern, line) for pattern in ignore):
            continue
        if any(pattern.search(line) for pattern in LOG_PROBLEMS):
            problems.append(line.strip()[:300])
    return problems


# Tiers ------------------------------------------------------------------------------------------

def tier0(directory: Path, results: Results):
    logs = directory / "t0"
    logs.mkdir(parents=True, exist_ok=True)
    check(results, "T0.1", gradle(["compileJava"], logs / "compileJava.log") == 0 and gradle(["build"], logs / "build.log") == 0)

    before = set(subprocess.run(["git", "status", "--porcelain", "src/generated"], cwd=REPO, capture_output=True, text=True).stdout.splitlines())
    datagen = gradle(["runClientData"], logs / "runClientData.log")
    after = subprocess.run(["git", "status", "--porcelain", "src/generated"], cwd=REPO, capture_output=True, text=True).stdout.splitlines()
    unexpected = [line for line in after if "loot_modifiers/" not in line and line not in before]
    check(results, "T0.2", datagen == 0 and not unexpected, f"unexpected generated changes: {unexpected}" if unexpected else "")

    bad_json = []
    configs = []
    for root in (REPO / "src" / "main" / "resources", REPO / "src" / "generated" / "resources"):
        for path in root.rglob("*.json"):
            try:
                data = json.loads(path.read_text())
            except ValueError as error:
                bad_json.append(f"{path}: {error}")
                continue
            if "worldgen/feature" in path.as_posix() and isinstance(data, dict) and "config" in data:
                configs.append(str(path))
    check(results, "T0.3", not bad_json and not configs, "; ".join(bad_json + configs))

    removed = ["neoforge.items", "neoforge.energy", "fluids.capability", "FluidAction", "ConfiguredFeature",
               "FeaturePlaceContext", "PerlinSimplexNoise", "CapabilityAdapters"]
    hits = []
    for path in (REPO / "src" / "main" / "java").rglob("*.java"):
        for number, line in enumerate(path.read_text().splitlines(), 1):
            if line.strip().startswith(("//", "*", "/*")):
                continue
            hits += [f"{path.name}:{number} {name}" for name in removed if name in line]
    check(results, "T0.4", not hits, "; ".join(hits[:20]))

    base = subprocess.run(["git", "merge-base", "HEAD", "26.2-dev"], cwd=REPO, capture_output=True, text=True).stdout.strip()
    diff = subprocess.run(["git", "diff", base, "--", "src/main/java", "src/gametest/java"], cwd=REPO, capture_output=True, text=True).stdout
    added = [line[1:] for line in diff.splitlines() if line.startswith("+") and not line.startswith("+++")]
    untracked = subprocess.run(["git", "ls-files", "--others", "--exclude-standard", "src/main/java", "src/gametest/java"],
                               cwd=REPO, capture_output=True, text=True).stdout.split()
    for path in untracked:
        added += (REPO / path).read_text().splitlines()
    style = []
    for line in added:
        code = line.strip()
        if code.startswith("import "):
            if code.startswith("import javax.annotation"):
                style.append(f"javax.annotation import: {code}")
            continue
        if re.search(r"\bcom\.veteam\.\w", code) and not code.startswith("package "):
            style.append(f"qualified com.veteam name: {code}")
        if "System.out" in code:
            style.append(f"System.out: {code}")
        if (code.startswith("//") or code.startswith("*")) and (" -- " in code or " \u2013 " in code):
            style.append(f"spaced dash: {code}")
    check(results, "T0.5", not style, "; ".join(style[:20]))

    validate = subprocess.run(["openspec", "validate", "port-to-26-3", "--strict"], cwd=REPO, capture_output=True, text=True)
    check(results, "T0.6", validate.returncode == 0, validate.stdout.strip().splitlines()[-1] if validate.stdout.strip() else validate.stderr.strip())


def neoform_directory(version):
    candidates = sorted((REPO / "build" / "neoForm").glob(f"neoFormJoined{version}-*"))
    return candidates[-1] if candidates else None


def vanilla_classpath(neoform: Path):
    libraries = [line[3:] for line in (neoform / "steps" / "listLibraries" / "libraries.txt").read_text().splitlines() if line.startswith("-e=")]
    return os.pathsep.join([str(neoform / "steps" / "setup" / "outputs.jar")] + libraries)


def run_java_tool(source: Path, classpath, work: Path, args):
    work.mkdir(parents=True, exist_ok=True)
    compiled = subprocess.run(["javac", "-d", str(work), "-cp", classpath, str(source)], capture_output=True, text=True)
    if compiled.returncode != 0:
        raise RuntimeError(compiled.stderr)
    ran = subprocess.run(["java", "-cp", os.pathsep.join([str(work), classpath]), source.stem, *args], capture_output=True, text=True)
    if ran.returncode != 0:
        raise RuntimeError(ran.stderr)
    return ran.stdout


def tier1(directory: Path, results: Results, old_neoform=None):
    tools = Path(__file__).resolve().parent / "t1"
    new = neoform_directory("26.3")
    old = old_neoform or neoform_directory("26.2")
    if old is None or new is None:
        results.record("T1.1", "skip", "26.2 or 26.3 NeoForm workspace missing; run t4 first to build the 26.2 worktree")
        results.record("T1.2", "skip", "same")
        return
    noise_json = REPO / "src" / "main" / "resources" / "data" / MODID / "worldgen" / "noise" / "petroleum_reservoir_boundary.json"
    worst = 0.0
    try:
        for seed in ("12345", "-987654321", SEED):
            old_values = run_java_tool(tools / "OldNoise.java", vanilla_classpath(old), directory / "t1" / "old", [seed]).split()
            new_values = run_java_tool(tools / "NewNoise.java", vanilla_classpath(new), directory / "t1" / "new", [seed, str(noise_json)]).split()
            if len(old_values) != len(new_values):
                raise RuntimeError("sample counts differ")
            worst = max(worst, max(abs(float(a) - float(b)) for a, b in zip(old_values, new_values)))
        check(results, "T1.1", worst <= 1e-5, f"max |difference| {worst:.3g} over {len(old_values)} points per seed")
    except RuntimeError as error:
        results.record("T1.1", "fail", error)

    classes = ["net/minecraft/util/RandomSource.java", "net/minecraft/world/level/levelgen/XoroshiroRandomSource.java",
               "net/minecraft/world/level/levelgen/LegacyRandomSource.java", "net/minecraft/world/level/levelgen/WorldgenRandom.java",
               "net/minecraft/world/level/levelgen/PositionalRandomFactory.java"]
    differing = []
    for name in classes:
        sources = []
        for neoform in (old, new):
            listing = subprocess.run(["unzip", "-p", str(neoform / "steps" / "decompile" / "outputs.jar"), name], capture_output=True)
            sources.append(listing.stdout)
        if not sources[0] or sources[0] != sources[1]:
            differing.append(name.rsplit("/", 1)[-1])
    check(results, "T1.2", not differing, f"differ or missing: {differing}" if differing else f"{len(classes)} classes identical")
    tunnel_parity(results, old, new)


def method_body(source, name):
    start = re.search(rf"\n    (?:private |public |protected )?(?:static )?[\w<>, ]+ {name}\(", source)
    if start is None:
        return None
    depth = 0
    for index in range(source.index("{", start.end()), len(source)):
        depth += {"{": 1, "}": -1}.get(source[index], 0)
        if depth == 0:
            return source[source.index("{", start.end()):index + 1]
    return None


def normalize_tracing(body):
    body = body.replace("config.", "").replace("this.getRange()", "TUNNEL_RANGE")
    return re.sub(r"\s+", " ", body).strip()


def tunnel_parity(results, old, new):
    """T1.3: the 26.3 cave feature traces tunnels with the 26.2 carver's code and unchanged vanilla helpers."""
    carver = subprocess.run(["git", "show", "26.2-dev:src/main/java/com/veteam/voluminousenergy/world/carver/VEFluidCaveCarver.java"],
                            cwd=REPO, capture_output=True, text=True).stdout
    feature = (REPO / "src" / "main" / "java" / "com" / "veteam" / MODID / "world" / "feature" / "VEFluidCaveFeature.java").read_text()
    differing = []
    for method in ("traceOutline", "traceTunnel", "tunnelThickness"):
        old_body, new_body = method_body(carver, method), method_body(feature, method)
        if old_body is None or new_body is None or normalize_tracing(old_body) != normalize_tracing(new_body):
            differing.append(method)
    def vanilla(neoform, name):
        return subprocess.run(["unzip", "-p", str(neoform / "steps" / "decompile" / "outputs.jar"), name], capture_output=True, text=True).stdout

    for name in ["net/minecraft/util/valueproviders/UniformFloat.java", "net/minecraft/world/level/levelgen/heightproviders/UniformHeight.java"]:
        sources = [vanilla(neoform, name) for neoform in (old, new)]
        if not sources[0] or sources[0] != sources[1]:
            differing.append(name.rsplit("/", 1)[-1])
    # Mth gained unrelated methods in 26.3; the tunnels only use sin, cos, and their table.
    maths = [vanilla(neoform, "net/minecraft/util/Mth.java") for neoform in (old, new)]
    for part in ("sin", "cos"):
        if method_body(maths[0], part) is None or method_body(maths[0], part) != method_body(maths[1], part):
            differing.append(f"Mth.{part}")
    tables = [re.findall(r".*SIN.*", source) for source in maths]
    if not tables[0] or tables[0] != tables[1]:
        differing.append("Mth.SIN")
    check(results, "T1.3", not differing, f"differ: {differing}" if differing else "tracing methods and vanilla helpers unchanged")


def tier2(directory: Path, results: Results):
    log = directory / "t2" / "runGameTestServer.log"
    log.parent.mkdir(parents=True, exist_ok=True)
    code = gradle(["runGameTestServer"], log)
    text = log.read_text(errors="replace")
    summary = re.search(r"All (\d+) required tests passed|(\d+) required tests failed", text)
    failures = re.findall(rf"{MODID}:(\S+) failed at [^!]*! (.*)", text)
    for name, reason in failures:
        results.record(f"T2:{name}", "fail", reason)
    for name in sorted(set(re.findall(r"VE-AUTOTEST PASS (\S+)", text))):
        results.record(f"T2:{name}", "pass")
    check(results, "T2", code == 0 and not failures, summary.group(0) if summary else "no summary in the log")
    problems = scan_log(log)
    check(results, "T2.log", not problems, "; ".join(problems[:10]))


SURVEY_TESTS = ["wg_petroleum_caves", "wg_reservoirs", "wg_lakes_geysers", "wg_ores", "wg_rice", "wg_other_dimensions",
                "wg_fingerprint", "wg_fluid_scanner", "wg_reservoir_cache"]
SURVEY_IDS = {"wg_petroleum_caves": "T3.1-T3.4", "wg_reservoirs": "T3.5", "wg_lakes_geysers": "T3.6", "wg_ores": "T3.7",
              "wg_rice": "T3.8", "wg_other_dimensions": "T3.9", "wg_fingerprint": "T3.10", "wg_fluid_scanner": "T3.16", "wg_reservoir_cache": "T3.11"}


def survey(server: Server, pregen_tests, tests):
    outcomes = {}
    for pregen in pregen_tests:
        outcomes[pregen] = server.run_test(pregen)
        if not outcomes[pregen][0]:
            return outcomes
    for test in tests:
        outcomes[test] = server.run_test(test)
    return outcomes


def tier3(directory: Path, results: Results):
    logs = []
    radius = 20
    outcomes = {}
    reference = None
    for attempt in range(3):
        reference = Server(directory, f"ve-autotest-wg-{radius}", radius=radius)
        reference.prepare()
        if not reference.start():
            results.record("T3", "fail", f"server did not start, see {reference.log}")
            return
        outcomes = survey(reference, ["wg_pregen"], SURVEY_TESTS)
        values = {test: reference.values(test) for test in SURVEY_TESTS + ["wg_pregen"]}
        reference.stop()
        logs.append(reference.log)
        missing = [test for test, (passed, detail) in outcomes.items() if not passed and "not found" in detail]
        if not missing or attempt == 2:
            break
        radius += 20
    (directory / "t3-values.json").write_text(json.dumps(values, indent=2))
    results.record("T3.seed", "info", f"seed {SEED}, survey radius {radius} chunks ({2 * radius + 1}x{2 * radius + 1})")
    for test, (passed, detail) in outcomes.items():
        results.record(SURVEY_IDS.get(test, test), "pass" if passed else "fail", f"{test}: {detail}")
    fingerprint = values.get("wg_fingerprint", {}).get("sha256")

    startup_template = reference.directory / STARTUP_CONFIG
    uncached = Server(directory, "ve-autotest-wg-uncached", radius=radius, startup_edits={"Cache Petroleum Reservoir Cells": "false"})
    uncached.prepare(startup_template)
    if uncached.start():
        result = survey(uncached, ["wg_pregen"], ["wg_reservoirs"])
        uncached.stop()
        logs.append(uncached.log)
        passed, detail = result.get("wg_reservoirs", (False, "pregen failed"))
        results.record("T3.11.uncached", "pass" if passed else "fail", f"reservoirs with caching off: {detail}")
    else:
        results.record("T3.11.uncached", "fail", "server did not start")

    shuffled = Server(directory, "ve-autotest-wg-shuffled", radius=radius)
    shuffled.prepare()
    if shuffled.start():
        first = shuffled.run_test("wg_pregen_random_first_half")
        shuffled.stop()
        logs.append(shuffled.log)
        if shuffled.start():
            second = survey(shuffled, ["wg_pregen_random_second_half"], ["wg_petroleum_caves", "wg_reservoirs", "wg_fingerprint"])
            other = shuffled.values("wg_fingerprint").get("sha256")
            shuffled.stop()
            logs.append(shuffled.log)
            # Vanilla ore placement, oil lakes, geysers, and reservoir plugs depend on the order chunks
            # generate in, so the fingerprint is recorded but only the sealing invariants must hold.
            sealed = all(second.get(test, (False, ""))[0] for test in ("wg_petroleum_caves", "wg_reservoirs"))
            check(results, "T3.12", first[0] and sealed,
                  f"fingerprint {'equal' if other == fingerprint else 'differs'} ({other} vs {fingerprint}); {second}")
        else:
            results.record("T3.12", "fail", "server did not restart")
    else:
        results.record("T3.12", "fail", "server did not start")

    override = Server(directory, "ve-autotest-wg-override", radius=radius, datapacks={"ve-autotest-override": override_datapack()})
    override.prepare()
    if override.start():
        result = survey(override, ["wg_pregen"], ["wg_datapack_override"])
        values = override.values("wg_datapack_override")
        override.stop()
        logs.append(override.log)
        passed, detail = result.get("wg_datapack_override", (False, "pregen failed"))
        results.record("T3.13", "pass" if passed else "fail", f"{detail} {values}" if values else detail)
    else:
        results.record("T3.13", "fail", "server did not start")

    invalid = Server(directory, "ve-autotest-wg-invalid", radius=radius, datapacks={"ve-autotest-invalid": invalid_datapack()})
    invalid.prepare()
    started = invalid.start(timeout=900)
    invalid.stop()
    text = invalid.log.read_text(errors="replace")
    check(results, "T3.14", not started and "a cap reservoir cannot reach the surface" in text,
          "server refused the datapack and named the problem" if not started else "server started with an invalid reservoir")

    fast = Server(directory, "ve-autotest-wg-fast", radius=radius, startup_edits={"Fast Petroleum Reservoir Surface Sampling": "true"})
    fast.prepare(startup_template)
    if fast.start():
        result = survey(fast, ["wg_pregen"], ["wg_reservoirs"])
        values = fast.values("wg_reservoirs")
        fast.stop()
        logs.append(fast.log)
        passed, detail = result.get("wg_reservoirs", (False, "pregen failed"))
        results.record("T3.15", "pass" if passed else "fail", f"{detail} {values}")
    else:
        results.record("T3.15", "fail", "server did not start")

    # The geyser reaches past its neighbouring chunks; 1.21.1 and 26.2 log the same far-chunk writes.
    geyser = r"(far chunk|unsafe terrain read).*GeyserFeature"
    problems = []
    geyser_lines = 0
    for log in logs:
        problems += scan_log(log, ignore=[r"VE-AUTOTEST", geyser])
        geyser_lines += len(re.findall(geyser, log.read_text(errors="replace")))
    results.record("T3.17.geyser", "info", f"{geyser_lines} known geyser far-chunk lines")
    check(results, "T3.17", not problems, "; ".join(problems[:10]))


def override_datapack():
    feature = REPO / "src" / "main" / "resources" / "data" / MODID / "worldgen" / "feature"
    cave = json.loads((feature / "crude_oil_cave.json").read_text())
    cave["fluidstate"] = {"id": "minecraft:lava", "properties": {"level": "0"}}
    cave["biomes"] = "#minecraft:is_deep_ocean"
    lake = json.loads((feature / "surface_oil_lake.json").read_text())
    lake["fluidstate"] = {"id": "minecraft:lava", "properties": {"level": "0"}}
    modifier = {"type": "neoforge:add_features", "biomes": "#minecraft:is_overworld",
                "features": [f"{MODID}:crude_oil_cave"], "step": "raw_generation"}
    return {
        f"data/{MODID}/worldgen/feature/crude_oil_cave.json": cave,
        f"data/{MODID}/worldgen/feature/surface_oil_lake.json": lake,
        f"data/{MODID}/neoforge/biome_modifier/petroleum_caves_overworld.json": modifier,
    }


def invalid_datapack():
    feature = REPO / "src" / "main" / "resources" / "data" / MODID / "worldgen" / "feature"
    reservoir = json.loads((feature / "natural_gas_reservoir.json").read_text())
    reservoir["reaches_surface"] = True
    return {f"data/{MODID}/worldgen/feature/natural_gas_reservoir.json": reservoir}


# Save compatibility ----------------------------------------------------------------------------

COMPAT_ORIGIN = (10000, 150, 10000)


def parse_snbt(text):
    """Parses the SNBT that /data get prints into Python values; enough for machine block data."""
    position = 0

    def skip():
        nonlocal position
        while position < len(text) and text[position] in " \n\t,":
            position += 1

    def value():
        nonlocal position
        skip()
        char = text[position]
        if char == "{":
            position += 1
            result = {}
            while True:
                skip()
                if text[position] == "}":
                    position += 1
                    return result
                key = token()
                skip()
                position += 1  # ':'
                result[key] = value()
        if char == "[":
            position += 1
            if re.match(r"[BIL];", text[position:position + 2]):
                position += 2
            result = []
            while True:
                skip()
                if text[position] == "]":
                    position += 1
                    return result
                result.append(value())
        raw = token()
        number = re.fullmatch(r"(-?[0-9.]+(?:E-?[0-9]+)?)[bslfdBSLFD]?", raw)
        if number:
            return float(number.group(1)) if any(c in number.group(1) for c in ".E") else int(number.group(1))
        return raw

    def token():
        nonlocal position
        skip()
        if text[position] in "\"'":
            quote = text[position]
            position += 1
            out = []
            while text[position] != quote:
                if text[position] == "\\":
                    position += 1
                out.append(text[position])
                position += 1
            position += 1
            return "".join(out)
        start = position
        while position < len(text) and text[position] not in ",:}] \n":
            position += 1
        return text[start:position]

    return value()


def block_data(server, pos):
    response = server.rcon.command(f"data get block {pos[0]} {pos[1]} {pos[2]}")
    if "has the following block data:" not in response:
        raise RuntimeError(response)
    snbt = response.split("has the following block data:", 1)[1].strip()
    return snbt, parse_snbt(snbt)


def comparable(data, info):
    """The parts of a machine's data the port must keep: items, energy, tanks, and side settings."""
    items = sorted((int(item["Slot"]), item["id"], int(item.get("count", 1))) for item in data.get("inv", {}).get("Items", []))
    tanks = {tank["name"]: (data.get(tank["name"], {}).get("Fluid", {}).get("id"), data.get(tank["name"], {}).get("Fluid", {}).get("amount"))
             for tank in info["tanks"]}
    energy = data.get("energy", {}).get("energy")
    return {"items": items, "size": data.get("inv", {}).get("Size"), "energy": energy, "tanks": tanks,
            "slot_state": data.get("slot_state", {})}


def machine_layout(directory: Path, results: Results):
    log = directory / "t2" / "runGameTestServer.log"
    if not log.exists() or "compat_layout" not in log.read_text(errors="replace"):
        log.parent.mkdir(parents=True, exist_ok=True)
        gradle(["runGameTestServer"], log)
    layout = {name: json.loads(value) for name, value in re.findall(r"VE-AUTOTEST VALUE compat_layout (\S+)=(\{.*\})", log.read_text(errors="replace"))}
    if not layout:
        raise RuntimeError("compat_layout gave no machines")
    return layout


def machine_nbt(info):
    items = ",".join(f'{{Slot:{slot}b,id:"minecraft:cobblestone",count:{slot + 1}}}' for slot in range(info["size"]))
    parts = [f"inv:{{Items:[{items}],Size:{info['size']}}}"]
    if info["capacity"]:
        parts.append(f"energy:{{energy:{info['capacity'] // 3}}}")
    sides = []
    for index, tank in enumerate(info["tanks"]):
        parts.append(f'{tank["name"]}:{{Fluid:{{id:"minecraft:water",amount:{100 * (index + 1)}}}}}')
        sides.append(f"{tank['prefix']}_enabled:1b,{tank['prefix']}_direction:5")
    sides += [f"{manager}_enabled:1b,{manager}_direction:4" for manager in info["managers"]]
    parts.append("slot_state:{" + ",".join(sides) + "}")
    return "{" + ",".join(parts) + "}"


def tier4(directory: Path, results: Results):
    layout = machine_layout(directory, results)
    machines = sorted(layout)
    worktree = directory / "ve-26.2"
    if not worktree.exists():
        subprocess.run(["git", "worktree", "add", "--detach", str(worktree), "26.2-dev"], cwd=REPO, check=True, capture_output=True)
    x0, y0, z0 = COMPAT_ORIGIN
    positions = {name: (x0 + 2 * index, y0, z0) for index, name in enumerate(machines)}
    crusher = (x0, y0, z0 + 4)
    furnace = (x0 + 2, y0, z0 + 4)
    area = f"{x0} {z0} {x0 + 2 * len(machines)} {z0 + 4}"
    try:
        old = Server(directory, "ve-autotest-compat-26.2", repo=worktree)
        old.prepare()
        if not old.start(timeout=3600):
            results.record("T4.1", "fail", f"26.2 server did not start, see {old.log}")
            return
        for command in ["tick freeze", f"forceload add {area}"]:
            old.rcon.command(command)
        for name in machines:
            x, y, z = positions[name]
            old.rcon.command(f"setblock {x} {y} {z} {name}")
            response = old.rcon.command(f"data merge block {x} {y} {z} {machine_nbt(layout[name])}")
            if "Modified" not in response:
                results.record("T4.1", "fail", f"{name}: {response}")
        old.rcon.command(f"setblock {crusher[0]} {crusher[1]} {crusher[2]} {MODID}:crusher")
        old.rcon.command(f'data merge block {crusher[0]} {crusher[1]} {crusher[2]} {{inv:{{Items:[{{Slot:0b,id:"minecraft:ancient_debris",count:1}}],Size:4}}}}')
        old.rcon.command(f"setblock {furnace[0]} {furnace[1]} {furnace[2]} {MODID}:electric_furnace")
        old.rcon.command(f'data merge block {furnace[0]} {furnace[1]} {furnace[2]} {{inv:{{Items:[{{Slot:0b,id:"minecraft:raw_iron",count:2}}],Size:3}}}}')
        old.rcon.command("forceload remove all")
        old.rcon.command("save-all flush")
        old.stop()
        results.record("T4.1", "pass" if "T4.1" not in results.entries else results.entries["T4.1"]["status"],
                       f"{len(machines)} machines placed and filled on the 26.2 build")

        if not old.start(timeout=1800):
            results.record("T4.2", "fail", "26.2 server did not restart")
            return
        old.rcon.command("tick freeze")
        old.rcon.command(f"forceload add {area}")
        expected = {}
        fixtures = REPO / "src" / "gametest" / "resources" / "data" / MODID / "autotest_fixtures" / "26.2"
        fixtures.mkdir(parents=True, exist_ok=True)
        wrong = []
        for name in machines:
            snbt, data = block_data(old, positions[name])
            expected[name] = comparable(data, layout[name])
            (fixtures / f"{name.split(':')[1]}.snbt").write_text(snbt + "\n")
            if expected[name]["items"] != [(slot, "minecraft:cobblestone", slot + 1) for slot in range(layout[name]["size"])]:
                wrong.append(name)
        old.rcon.command("forceload remove all")
        old.stop()
        check(results, "T4.2", not wrong, f"captured {len(machines)} machines" if not wrong else f"26.2 did not keep the set contents of {wrong}")

        new = Server(directory, "ve-autotest-compat-26.3")
        new.prepare()
        if (new.directory / "world").exists():
            shutil.rmtree(new.directory / "world")
        shutil.copytree(old.directory / "world", new.directory / "world")
        started = new.start(timeout=1800)
        problems = scan_log(new.log, ignore=[r"VE-AUTOTEST"]) if new.log else ["no log"]
        upgrade = [line for line in new.log.read_text(errors="replace").splitlines()
                   if re.search(r"(Unknown|Skipping|missing).*(block ?entity|registry).*" + MODID, line, re.IGNORECASE)]
        check(results, "T4.3", started and not problems and not upgrade, "; ".join((problems + upgrade)[:10]))
        if not started:
            return
        new.rcon.command("tick freeze")
        new.rcon.command(f"forceload add {area}")
        mismatches = []
        for name in machines:
            _, data = block_data(new, positions[name])
            actual = comparable(data, layout[name])
            if actual != expected[name]:
                mismatches.append(f"{name}: {actual} != {expected[name]}")
        check(results, "T4.4", not mismatches, f"{len(machines)} machines equal" if not mismatches else "; ".join(mismatches[:5]))

        new.rcon.command("tick unfreeze")
        outputs = {}
        deadline = time.time() + 120
        while time.time() < deadline:
            # A full charge does not cover some recipes (crushing Ancient Debris takes 8080 FE), so keep them powered.
            for pos, machine in ((crusher, f"{MODID}:crusher"), (furnace, f"{MODID}:electric_furnace")):
                new.rcon.command(f"data merge block {pos[0]} {pos[1]} {pos[2]} {{energy:{{energy:{layout[machine]['capacity']}}}}}")
            outputs = {label: block_data(new, pos)[1].get("inv", {}).get("Items", [])
                       for label, pos in (("crusher", crusher), ("furnace", furnace))}
            done = (any(int(item["Slot"]) == 1 for item in outputs["crusher"])
                    and any(int(item["Slot"]) == 1 and item["id"] == "minecraft:iron_ingot" for item in outputs["furnace"]))
            if done:
                break
            time.sleep(5)
        check(results, "T4.5", done, f"outputs {outputs}")
        new.rcon.command("forceload remove all")
        new.stop()

        diff = subprocess.run(["git", "diff", "--stat", "26.2-dev", "--", f"src/main/java/com/veteam/{MODID}/persistence"],
                              cwd=REPO, capture_output=True, text=True).stdout.strip()
        check(results, "T4.6", not diff, diff or "persistence/ unchanged from 26.2-dev")

        log = directory / "t4" / "runGameTestServer-fixtures.log"
        log.parent.mkdir(parents=True, exist_ok=True)
        gradle(["runGameTestServer"], log)
        text = log.read_text(errors="replace")
        passed = re.search(r"VE-AUTOTEST PASS storage_26_2_fixtures (.*)", text)
        failed = re.search(rf"{MODID}:storage_26_2_fixtures failed at [^!]*! (.*)", text)
        check(results, "T2.3", passed is not None and failed is None, passed.group(1) if passed else (failed.group(1) if failed else "not run"))
    finally:
        subprocess.run(["git", "worktree", "remove", "--force", str(worktree)], cwd=REPO, capture_output=True)


# Client smoke ---------------------------------------------------------------------------------

CLIENT_WORLD = "ve-autotest-client"
CLIENT_OPTIONS = """onboardAccessibility:false
joinedFirstServer:true
skipMultiplayerWarning:true
tutorialStep:none
pauseOnLostFocus:false
renderDistance:6
narrator:0
soundCategory_master:0.0
"""


def build_area(server: Server):
    """One of every machine in a row, a sealed natural gas column, a working laser, and an oil pond, in front of spawn."""
    x0, y0, z0 = COMPAT_ORIGIN
    commands = [
        f"fill {x0 - 4} {y0 - 1} {z0 - 12} {x0 + 70} {y0 - 1} {z0 + 12} minecraft:stone",
        f"fill {x0 - 4} {y0} {z0 - 12} {x0 + 70} {y0 + 12} {z0 - 1} minecraft:air",
        f"setworldspawn {x0 + 8} {y0} {z0 - 10} 0 0",
        "gamerule respawn_radius 0",
        f"fill {x0 + 4} {y0} {z0 - 4} {x0 + 6} {y0 + 6} {z0 - 2} minecraft:glass hollow",
        f"setblock {x0 + 5} {y0 + 1} {z0 - 3} {MODID}:natural_gas_block",
        f"fill {x0 + 10} {y0} {z0 - 4} {x0 + 10} {y0 + 2} {z0 - 4} {MODID}:solarium_machine_casing",
        f"setblock {x0 + 10} {y0 + 3} {z0 - 4} {MODID}:dimensional_laser",
        f"fill {x0 + 13} {y0 - 1} {z0 - 6} {x0 + 17} {y0 - 1} {z0 - 2} {MODID}:bituminous_gravel",
        f"fill {x0 + 14} {y0 - 1} {z0 - 5} {x0 + 16} {y0 - 1} {z0 - 3} {MODID}:crude_oil_block",
        "gamerule advance_time false",
        "time set 6000",
    ]
    for command in commands:
        reply = server.rcon.command(command)
        if re.search(r"Unknown|Incorrect|Incomplete|Expected|Invalid|expected", reply):
            raise RuntimeError(f"build area command {command!r} failed: {reply}")


def tier5(directory: Path, results: Results):
    source = directory / "server" / "ve-autotest-compat-26.3" / "world"
    host = Server(directory, "ve-autotest-client-builder")
    host.prepare()
    if (host.directory / "world").exists():
        shutil.rmtree(host.directory / "world")
    if source.exists():
        shutil.copytree(source, host.directory / "world")
    if not host.start():
        results.record("T5.1", "fail", "could not start the server that builds the client world")
        return
    host.rcon.command(f"forceload add {COMPAT_ORIGIN[0] - 16} {COMPAT_ORIGIN[2] - 16} {COMPAT_ORIGIN[0] + 80} {COMPAT_ORIGIN[2] + 16}")
    build_area(host)
    host.rcon.command("forceload remove all")
    host.rcon.command("save-all flush")
    host.stop()

    client = directory / "client"
    saves = client / "saves" / CLIENT_WORLD
    if saves.exists():
        shutil.rmtree(saves)
    saves.parent.mkdir(parents=True, exist_ok=True)
    shutil.copytree(host.directory / "world", saves)
    (client / "options.txt").write_text(CLIENT_OPTIONS)
    init = client / "autotest-init.gradle"
    display = 97
    # SDL and GLFW find the user's Wayland compositor through its default socket even without
    # WAYLAND_DISPLAY, which puts the window on their desktop instead of the virtual display.
    write_init_script(init, "client", client, {}, ["--quickPlaySingleplayer", CLIENT_WORLD, "--width", "1280", "--height", "720"],
                      {"DISPLAY": f":{display}", "WAYLAND_DISPLAY": "autotest-no-wayland", "SDL_VIDEO_DRIVER": "x11",
                       "XDG_SESSION_TYPE": "x11", "LIBGL_ALWAYS_SOFTWARE": "1"})
    log = client / f"console-{int(time.time())}.log"
    level_dat = saves / "level.dat"
    started_at = time.time()
    with open(log, "w") as out:
        process = subprocess.Popen(["xvfb-run", f"--server-num={display}", "-s", "-screen 0 1280x720x24",
                                    "./gradlew", "-I", str(init), "--no-configuration-cache", "runClient"],
                                   cwd=REPO, stdout=out, stderr=subprocess.STDOUT)
    joined = False
    deadline = time.time() + 600
    while time.time() < deadline and process.poll() is None:
        if re.search(r"joined the game|Loaded \d+ advancements", log.read_text(errors="replace")) and \
                re.search(r"Starting integrated minecraft server|Done \(", log.read_text(errors="replace")):
            joined = True
            break
        time.sleep(3)
    check(results, "T5.1", joined, "client reached the world" if joined else f"client did not reach the world, see {log}")

    shots = client / "screenshots-autotest"
    shots.mkdir(exist_ok=True)
    if joined:
        time.sleep(20)
        for shot in range(6):
            subprocess.run(["import", "-window", "root", "-display", f":{display}", str(shots / f"shot-{shot}.png")], capture_output=True)
            time.sleep(10)
    results.record("T5.3", "info", f"{len(list(shots.glob('*.png')))} screenshots in {shots} for review")

    java = subprocess.run(["pgrep", "-f", f"quickPlaySingleplayer {CLIENT_WORLD}"], capture_output=True, text=True).stdout.split()
    for pid in java:
        subprocess.run(["kill", "-TERM", pid])
    try:
        process.wait(timeout=300)
    except subprocess.TimeoutExpired:
        process.kill()
    saved = level_dat.exists() and level_dat.stat().st_mtime > started_at
    check(results, "T5.5", saved, "level.dat updated on exit" if saved else "level.dat not updated")

    text = log.read_text(errors="replace")
    jei_errors = [line for line in text.splitlines() if "jei" in line.lower() and re.search(r"ERROR|Exception", line)]
    check(results, "T5.2", "jei" in text.lower() and not jei_errors, "; ".join(jei_errors[:5]) or "JEI started without errors")
    # 1.21.1 logs the same: 32 storage and fluid blocks without a blockstate model, rice_crop's
    # blockstate, and the two tank textures.
    known = [r"Missing model for variant: 'Block\{voluminousenergy:", r"Exception loading blockstate definition: 'voluminousenergy:rice_crop",
             r"Missing textures in model voluminousenergy:(block|item)/(netherite|nighalite)_tank"]
    problems = scan_log(log, ignore=[r"VE-AUTOTEST"] + known)
    known_lines = sum(len(re.findall(pattern, text)) for pattern in known)
    results.record("T5.4.known", "info", f"{known_lines} known model warnings that 1.21.1 logs too")
    check(results, "T5.4", not problems, "; ".join(problems[:10]))


# Report -----------------------------------------------------------------------------------------

def report(directory: Path, results: Results):
    lines = ["# Autotest report", "", f"Directory: `{directory}`", "", "| Test | Result | Detail |", "|---|---|---|"]
    for test_id, entry in results.entries.items():
        detail = entry["detail"].replace("|", "\\|").replace("\n", " ")
        lines.append(f"| {test_id} | {entry['status']} | {detail[:400]} |")
    screenshots = sorted((directory / "client").glob("screenshots-autotest/*.png")) if (directory / "client").exists() else []
    if screenshots:
        lines += ["", "## Screenshots", ""] + [f"- `{path}`" for path in screenshots]
    (directory / "report.md").write_text("\n".join(lines) + "\n")
    print(directory / "report.md")


def main():
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--dir", required=True, type=Path, help="throwaway directory for every world, log, and report")
    parser.add_argument("tier", choices=["snapshot", "verify", "t0", "t1", "t2", "t3", "t4", "t5", "report", "all"])
    args = parser.parse_args()
    directory = args.dir.resolve()
    if REPO in directory.parents and "runs" in directory.relative_to(REPO).parts:
        sys.exit("--dir must not be inside runs/")
    directory.mkdir(parents=True, exist_ok=True)
    results = Results(directory)
    tiers = {"t0": tier0, "t1": tier1, "t2": tier2, "t3": tier3, "t4": tier4, "t5": tier5}
    try:
        if args.tier == "snapshot":
            snapshot(directory)
        elif args.tier == "verify":
            verify(directory, results)
        elif args.tier == "report":
            report(directory, results)
        elif args.tier == "all":
            snapshot(directory)
            for name in ("t0", "t1", "t2", "t3", "t4", "t5"):
                if name in tiers:
                    tiers[name](directory, results)
            verify(directory, results)
            report(directory, results)
        else:
            tiers[args.tier](directory, results)
    finally:
        for server in list(RUNNING):
            server.stop(timeout=60)
    sys.exit(1 if results.failed() else 0)


if __name__ == "__main__":
    main()
