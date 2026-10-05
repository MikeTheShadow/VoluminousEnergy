## Release process (manual, no CI)

- Cut release branch `1.21.1-<modver>` (e.g. `1.21.1-0.5`) from `1.21.1-dev`. Later Alphas, Betas, and hotfixes
  are new commits on it.
- One commit bumps `mod_version` in `gradle.properties` (`neoforge.mods.toml` expands `${mod_version}`).
  Message: `Release <version>`.
- Version string has no `v`, spaces allowed: `1.21.1-0.5 Alpha 1`. Hotfix adds a letter: `Alpha 1a`.
  Order: Alpha N -> Beta N -> stable `0.x.0.0`.
- `./gradlew clean publish` writes jar, pom, `.module` and checksums to
  `repo/com/veteam/voluminousenergy/VoluminousEnergy/<version>/` (gitignored).
  Verify: `unzip -p <jar> META-INF/neoforge.mods.toml | grep ^version` shows the version string.
- GitHub: tag `v<version>` with spaces as `_` (`v1.21.1-0.5_Alpha_1`), title `Voluminous Energy v<version>`,
  `--target` the release branch, Alpha/Beta always `--prerelease`. Attach every file in the version folder.
- Notes: CurseForge/Modrinth draft has a `# Voluminous Energy v...` title and `##` sections; for GitHub drop the
  title and promote `##` to `#`. Sections: `# Additions`, `# Fixes`, `# Changes`.
