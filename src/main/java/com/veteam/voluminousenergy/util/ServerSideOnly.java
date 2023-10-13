package com.veteam.voluminousenergy.util;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * This indicates the method/class is only safe to use on the server
 * attempting to call the method in client-side code will result in an error
 */
@Retention(RetentionPolicy.SOURCE)
public @interface ServerSideOnly {
}
