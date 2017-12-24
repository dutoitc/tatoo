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
Note that only jobs, services and routes in major version are built.

Examples:
- ch.mno.tatoo.builder.Main tatoo.properties 18.12.0 ignoreVersionCheck F_Crypto.*


(documentation under work)
