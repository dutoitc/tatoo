CONTEXT_FILE=context.properties
VERSION=$1

java -classpath uber-* DeployerMain $CONTEXT_FILE $VERSION
