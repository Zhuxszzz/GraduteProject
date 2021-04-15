package com.niantch.graproject.utils.permission;

import java.util.List;

/**
 * Created by Dell on 2018/1/2.
 */

public interface PermissionListener {
    void onGranted();

    void onDenied(List<String> deniedPermission);

    void onShouldShowRationale(List<String> deniedPermission);
}
