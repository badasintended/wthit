@rem Run this command as administrator

@rem Create a symlink to common source on platform impl
mklink /D %~dp0\fabric\src\common %~dp0\common\src\main
mklink /D %~dp0\forge\src\common %~dp0\common\src\main
