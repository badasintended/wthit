{ pkgs, lib, config, inputs, ... }:

{
  env.LD_LIBRARY_PATH = lib.makeLibraryPath (with pkgs; [
    libpulseaudio
    libGL
    glfw
    openal
    stdenv.cc.cc
    flite
  ]);
}
