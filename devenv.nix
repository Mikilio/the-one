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
  tasks = {
    "app:sim:build" = {
      exec = "./compile.sh";
      execIfModified = [
        "src"
      ];
    };
    # "app:sim:prepare:build" = {
    #   exec = "javac -sourcepath toolkit -d target -cp lib/jgrapht-core-1.5.2.jar toolkit/apocalypseSettingsGenerator/*.java src/core/Coord.java";
    #   execIfModified = [
    #     "toolkit/apocalypseSettingsGenerator"
    #   ];
    # };
    # "app:sim:prepare" = {
    #   exec = "java -Xmx512M -cp target:lib/jgrapht-core-1.5.2.jar apocalypseSettingsGenerator.MainZombieApocalypse";
    #   after = ["app:sim:prepare:build"];
    # };
    "app:sim" = {
      exec = ''
        chmod +x ./run_apocalypse_simulations.sh
        echo "You can now run the simulations with ./run_apocalypse_simulations.sh";
      '';
      after = [
        # "app:sim:prepare"
        "app:sim:build"
      ];
    };
  };
}
