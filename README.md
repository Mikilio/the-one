# The ONE

The Opportunistic Network Environment simulator.

For introduction and releases, see [the ONE homepage at GitHub](http://akeranen.github.io/the-one/).

For instructions on how to get started, see [the README](https://github.com/akeranen/the-one/wiki/README).

The [wiki page](https://github.com/akeranen/the-one/wiki) has the latest information.

This fork This project simulates a zombie outbreak using a modular, room-based architecture on top of the the ONE simulator.

The project is accompanied by a [report](.report.pdf).

Documentation for the ONE simulator is also applicable to this project.

## Use devenv

### 1. Install [Nix](https://nixos.org)

=== "Linux"

    ```
    sh <(curl -L https://nixos.org/nix/install) --daemon
    ```

=== "macOS"

    ```
    curl -L https://github.com/NixOS/experimental-nix-installer/releases/download/0.27.0/nix-installer.sh | sh -s -- install
    ```

    !!! note "Experimental installer"
        We recommend using the above experimental installer.
        It can handle OS upgrades and has better support for Apple silicon.

        If you'd like to stick with the official release installer, use:
        ```
        sh <(curl -L https://nixos.org/nix/install)
        ```

    **Upgrade Bash**

    macOS ships with an ancient version of Bash due to licensing reasons.

    We recommend installing a newer version from nixpkgs to avoid running into evaluation errors.

    === "Nix env (newcomers)"

        ```
        nix-env --install --attr bashInteractive -f https://github.com/NixOS/nixpkgs/tarball/nixpkgs-unstable
        ```

    === "Nix profiles (requires experimental flags)"

        ```
        nix profile install nixpkgs#bashInteractive
        ```

=== "Windows (WSL2)"

    ```
    sh <(curl -L https://nixos.org/nix/install) --no-daemon
    ```

=== "Docker"

    ```
    docker run -it nixos/nix
    ```


### 2. Install [devenv](https://github.com/cachix/devenv)


=== "Newcomers"

    ```
    nix-env --install --attr devenv -f https://github.com/NixOS/nixpkgs/tarball/nixpkgs-unstable
    ```

=== "Nix profiles (requires experimental flags)"

    ```
    nix profile install nixpkgs#devenv
    ```

=== "NixOS/nix-darwin/home-manager"

    ```nix title="configuration.nix"
    environment.systemPackages = [
      pkgs.devenv
    ];
    ```

## Build Manually
The Simulator itself can be build using the build script provided by the one Simulator.

For this you need to install the toolchain referenced in the documentation for the ONE simulator.
To compile the generator utility you can use the command:
```
```

```
javac -sourcepath toolkit -d target -cp lib/jgrapht-core-1.5.2.jar toolkit/apocalypseSettingsGenerator/*.java src/core/Coord.java
```

and to run it:

```
java -Xmx512M -cp target:lib/jgrapht-core-1.5.2.jar apocalypseSettingsGenerator.MainZombieApocalypse
```

## Run the complete simulation

Ensure the below script is executable and run:

```
./run_apocalypse_simulations.sh
```
