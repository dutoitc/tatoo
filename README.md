# tatoo
TAlend deployment TOOls, (or something like Tool Acting on Talend Open Operations), a command-line client to operate on Talend environement to install, start/stop, deploy, uninstal undeploy and monitor a Talend application.

# Conventions
- Nexus releases are stored/read in repositories/releases

# Builder
Tatoo Builder use Commandline to build some job, services and route from SVN and publish them on Nexus.

## Syntaxe
ch.mno.tatoo.builder.Main [context.properties] [publishedVersion] [ignoreVersionCheck]? [regex=...]?
- context.properties: Tatoo properties file. Check template at base/data/properties-template.properties
- publishedVersion: Nexus version published, like 1.2.3 or 18.12.0
- ignoreVersionCheck: By default, builder forbid publishing on nexus artifacts where its version is already present at Nexus. Use 'ignoreVersionCheck' to avoid this check.
- regex: regex double-check on fullname (path+name) or name: any win

Notes:
- that only jobs, services and routes in major version are built.
- jobs should be prefixed by SOCLE, PREDEPLOY, POSTDEPLOY_, JOB_, INFRA, WS to be deployed, otherwises they are ignored (convention to tell if a job is a root job or a subjob)
- with ignoreVersionCheck activated, if anything is already present on Nexus, it is simply not built. This is to ensure the rule that Release versions must be pushed only once on Nexus.

Examples:
- ch.mno.tatoo.builder.Main tatoo.properties 18.12.0 ignoreVersionCheck F_Crypto.*


# Deployer
Tatoo Deployer read jobs, services and routes on Nexus, clean Karaf and TAC and use Metaservlet to deploy.
This tool is deprecated and only kept for historical purpose. Please use tatoo-cli to deploy.


# Tatoo-CLI
Command-Line Interface to ease operations on a Talend server: build, install, deploy, stop, start, undeploy, delete, health check.

Usage:
ch.mno.tatoo.cli.TatooCliMain [--help] [--properties=...] [--json] [--onlystatus] [--dryrun] [...]
- --help Display command-line usage, generated dynamically
- --properties: Tatoo properties file. Check template at base/data/properties-template.properties
- --json or --onlystatus: Each operation on TatooCli is written to a reporter, which by default is console. --onlystatus is for health check report ("OK" or "KO ..."), --json output informations on JSON format (useful for displaying status on Monitoring screen)
- --dryrun operations on server are written but not executed (except for non-destructive operations like list). This is a simulation mode.
- ... Some commands provides more verbs and parameters, check --help for full syntax.

## Cleanup
Syntaxe: cleanup [regex]: 
- Delete execution plan using cleaned jobs (can't clean only job if a link exist)
- Delete tasks on TAC (jobs)
- Delete ESBTasks on TAC, Karaf (services, routes)
- Delete Karaf-related bundle, features, featureRepo

## Cron
Syntaxe: createCronTrigger [task] [label] [description|hours|minutes|daysOfMonth|daysOfWeek|months|years]*
- Create CRON on en existing task
- By default, time value is "*" if not specified

Example: 
- createCronTrigger myTask "my wonderful task" description="more description" hours=13 minutes=0,30

## Deploy
Syntaxe: 
- deploy [regex]
- deploy:routes [regex]
- deploy:services [regex]
- deploy:jobs [regex]
Deploy routes, services, jobs using a pattern (in this order). Regex is on application name as defined on TAC.
Use alternate forms to deploy only a kind of task.

## ESB Tasks
Syntaxe:
- start [regex]
- start:services [regex]
- start:routes [regex]
- stop [regex]
- stop:services [regex]
- stop:routes [regex]
Start/Stop services or routes based on regex on Application Name, using TAC Metaservlet. Order for start is service then route; Order for stop is route then service, so that services are already started for route calling them.

## Install
Syntaxe:
- install [version] [regex]
Install jobs, routes, services matching regex, without deploying them (only TAC configuration).
Version match 'startWith' rules: install 18.22 will install 18.22.3 if Nexus has version 18.22.1, 18.22.2 and 18.22.3 (higher minor wins)

## Status
Syntaxe:
- status
(Need some rework to make it generic yet)

## Undeploy
Syntaxe:
- undeploy [regex]
- undeploy:routes [regex]
- undeploy:services [regex]
Undeploy routes and services (already installed). The esbTasks are not cleaned. (See clean for that)


# Extending Tatoo
- Extend AbstractCommand class to add new commands (please submit good one to Tatoo !)
- ... more to come ?


# Want to help on Tatoo ?
You can:
- Send issues
- Send ideas, comments
- Send patch requests, fork
