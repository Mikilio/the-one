{
  pkgs,
  lib,
  config,
  inputs,
  ...
}: {
  # https://devenv.sh/packages/
  packages = [pkgs.git pkgs.google-java-format];
  languages = {
    java.enable = true;
    perl.enable = true;
  };
}
