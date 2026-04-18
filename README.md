# CraftyMason

CraftyMason is a Java/MASON-based land-use change simulation framework built around the CRAFTY modeling concepts. It simulates how land managers (agent functional types, or AFTs) compete for cells on a landscape while trying to satisfy ecosystem-service demand, and it supports adaptive policy interventions via fuzzy logic (taxes, subsidies, and protection rules).

## What this project does

At a high level, each simulation tick:

1. Loads/updates annual capitals and demand from CSV input data.
2. Computes utility as the gap between demand and current supply per service.
3. Lets managers evaluate and potentially take over land cells based on competitiveness.
4. Recomputes total service supply after ownership changes.
5. Lets institutions adapt policies (via fuzzy inference) and influence utility and land protection.
6. Optionally updates visualization (grid map and charts).

Core simulation orchestration is implemented with MASON `SimState` and scheduled steppables.

---

## Project structure

```text
src/main/java/
  crafty/         # Core data model: cells, managers, data loading, utility/production math
  updaters/       # Scheduled update pipeline (capital, demand, utility, supply, map)
  institution/    # Policy actors and policy objects (agri + nature institutions)
  modelRunner/    # Simulation entry points and base runner
  display/        # MASON UI and JFreeChart live charts
  experiments/    # Experiment-specific runners/parameter exposure
  test/           # Fuzzy rule test harness
resources/fcl/    # FCL fuzzy rule definitions
libs/             # Bundled third-party jars
in.params         # Example parameter sweep config
pom.xml           # Maven build config
```

---

## Architecture overview

### 1) Runner and scheduling layer

- `AbstractModelRunner` extends MASON `SimState`, owns a `stateManager` list, and drives `setup()` + scheduling for all model states.
- `ModelRunner` is the default concrete runner and wires a default state pipeline.
- `experiments.Intra` and `experiments.IndividualPolicies` override the state pipeline for scenario-specific experiments.

The execution order is controlled by insertion order in `stateManager` and scheduled priorities via `modelRunner.indexOf(this)`.

### 2) Data and domain layer (`crafty`)

- `DataCenter`: central hub for reading CSV inputs, maintaining service/capital names, demand/supply/utility maps, and AFT counters.
- `LandCell`/`AbstractCell`: represent grid cells with capital values, production filters, and current owner.
- `Manager`/`AbstractManager`: represent agents that produce services from owned land and compete for ownership changes.
- `Methods`: production and competitiveness calculations.
- `ManagerSet` and `CellSet`: collections that also participate in model lifecycle (`ModelState`).

### 3) Update pipeline (`updaters`)

- `SupplyInitializer` (one-time at tick 0): stores baseline supply.
- `CapitalUpdater`: applies next annual capital raster/table.
- `DemandUpdater`: applies next annual demand row.
- `UtilityUpdater` / `InfluencedUtilityUpdater`: computes utility from demand-supply gap; influenced variant also applies institutional policy effects.
- `SupplyUpdater`: recomputes aggregate supply after agent actions.
- `MapUpdater`: fills the `IntGrid2D` visualization map by AFT index.

### 4) Institutions and policy (`institution`)

- `AbstractInstitution`: common lifecycle for policy systems.
- `AgriInstitution`: adaptive policies targeting agricultural services (e.g., Meat, Crops).
- `NatureInstitution`: adaptive biodiversity/protection-oriented policies.
- `Policy`: builder-based policy object with lag, inertia, intervention history, and goal.
- Fuzzy logic rules are loaded from `resources/fcl/fuzzyPolicy.fcl` using jFuzzyLogic.

### 5) UI and display (`display`)

- `ModelRunnerWithUI`: MASON GUI wrapper and 2D grid visualization.
- `GridOfCharts`: JFreeChart frame with time series of supply vs demand per service.

---

## Data model and required inputs

The model expects CSV-based inputs for:

1. **Service definitions** (`Services.csv`)
   - Includes at least `Name` and `Index` columns.
2. **Capital definitions** (`Capitals.csv`)
   - Includes at least `Name` column.
3. **Agent sensitivity files** (directory of CSVs)
   - One file per AFT; file name becomes `managerType`.
   - Must include `Production` plus capital columns corresponding to `Capitals.csv`.
4. **Baseline map** (cell-level CSV)
   - Uses at least `x`, `y`, `FR` and capital columns.
   - `FR` maps each cell to an initial AFT file name.
5. **Annual capital directory**
   - Sequence of CSVs with per-cell capital updates keyed by `x`, `y`.
6. **Annual demand CSV**
   - Time rows; each service name is a column.

### Important path note

`ModelRunner` currently hard-codes Windows-style absolute paths for these files. You will almost certainly need to override these in your experiment class or update the defaults before running locally.

---

## Build and run

## Prerequisites

- Java 17
- Maven 3.8+

The project references several dependencies and includes jars in `libs/`. Depending on your local Maven setup, you may need to ensure all required artifacts are resolvable.

## Compile

```bash
mvn -DskipTests compile
```

## Run a main class with Maven exec plugin

You can choose the entry point using `exec.mainClass`:

```bash
mvn -DskipTests exec:java -Dexec.mainClass=experiments.Intra
```

Other useful entry points:

- `modelRunner.ModelRunner` (default non-experiment runner)
- `experiments.IndividualPolicies`
- `display.ModelRunnerWithUI` (GUI console)
- `test.TestTipper2` (fuzzy rule debugging)

---

## Simulation lifecycle in detail

1. `start()` in `AbstractModelRunner` clears stale states, rebuilds the state list, runs setup for each component, and schedules them.
2. `DataCenter.setup()`:
   - reads names and mappings,
   - loads AFT sensitivities,
   - loads baseline cells and assigns initial managers,
   - initializes maps for zero production, utility strategy, demand iterator, capital iterator.
3. Tick updates run via scheduled updaters and manager/institution steps.
4. Managers compute production and (for representative managers) search/compete for land takeovers.
5. Institutions monitor demand-supply outcomes, update policy interventions via fuzzy systems, and modify utility/protection outcomes.

---

## Parameterization and experimentation

`experiments.Intra` exposes many getters/setters that are convenient for MASON batch runs and UI parameter panels, including:

- policy goals (`meatGoal`, `cropGoal`, `divGoal`)
- policy lag
- threshold values
- output metrics (service supplies and AFT counts)

`in.params` provides a sample sweep configuration targeting:

- model: `experiments.Intra`
- independent variable: `PolicyLag`
- dependent metric: `IP`
- trial count, threads, steps, and output CSV target

---

## Known limitations / code quality notes

- Several values and assumptions are currently hard-coded (service names like `Meat`, `Crops`, `Diversity`; specific AFT labels).
- File paths in `ModelRunner` are environment-specific (Windows absolute paths).
- Some classes are placeholders or partially implemented (`Bugdet`, some TODO methods, `addAll` overrides returning `false`).

---

## Extending the model

Typical extension points:

1. Add a new institution by extending `AbstractInstitution` and registering it in a runner’s `loadStateManager()`.
2. Add new update stages by implementing `AbstractUpdater` and inserting them in state order.
3. Add new services/capitals through input CSVs and matching sensitivity columns.
4. Introduce new scenarios by subclassing `ModelRunner` in `experiments/` and exposing parameters via getters/setters.

---

## License

No license file is currently present in the repository. If you intend to distribute or publish this project, add an explicit license.
