jarpath=target/asm-loading-1.0-SNAPSHOT.jar

if [ ! -f $jarpath ]; then
  dir=$(pwd)

  cd ../loading-util; mvn install

  cd "$dir"
  mvn package
fi

java -Xmx32M -Xdebug \
  -verbose:class -XX:+TraceClassLoading -XX:+TraceClassUnloading \
  -XX:MaxMetaspaceSize=20240K \
  -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:gc.log \
  -jar $jarpath

