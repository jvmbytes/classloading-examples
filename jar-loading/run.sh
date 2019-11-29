dir=$(pwd)

jarpath=target/jar-loading-1.0-SNAPSHOT.jar

loadJar=$dir"/../demo-classes/target/demo-classes-1.0-SNAPSHOT.jar"
className="com.jvmbytes.classloading.demo.User"

if [ ! -f $jarpath ]; then
  cd ../loading-util; mvn install

  cd "$dir" ; mvn package
fi

java -Xmx128M -Xdebug -verbose:gc -XX:+TraceClassLoading -XX:+TraceClassUnloading \
  -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:gc.log \
  -jar $jarpath "$loadJar" $className

