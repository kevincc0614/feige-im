#!/usr/bin/env bash
### 第一个参数
JAR_NAME=feige-im-1.0.0-SNAPSHOT.jar
PROFILE=$2
JVM_OPTS=""
LOG_PATH="server.log"
VM_OPTIONS_FILE="vm-$PROFILE.options"
##检查该对象是否存在
checkPid(){
    pid=`ps -ef |grep ${JAR_NAME} |grep -v grep |awk '{print $2}'`
}

#检查程序是否在运行
status(){
   checkPid
   if [[ ! -n "$pid" ]]; then
        echo "$JAR_NAME is not running $logOutputFormat"
   else
        echo "$JAR_NAME is running  PID  $pid  ：execution date  $logOutputFormat"
   fi
}

loadVMOptions(){
for line in `cat $VM_OPTIONS_FILE`
do
    echo $line
    JVM_OPTS="$JVM_OPTS $line"
done
}
# 启动脚本
start(){
  checkPid
  if [[ ! -n "$pid" ]]; then
    loadVMOptions
    nohup java -server ${JVM_OPTS} -jar  ${JAR_NAME} > /dev/null 2>&1 &
    echo "---------------------------------"
    echo "启动完成，按CTRL+C退出日志界面即可>>>>>"
    echo "---------------------------------"
    sleep 3s
    tail -f ${LOG_PATH}
  else
      echo "$JAR_NAME is running PID: $pid"
  fi
}

## 停止脚本
stop(){
    checkPid
    if [ ! -n "$pid" ]; then

     echo "$JAR_NAME not running"
    else

      echo "$JAR_NAME stop..."
      kill ${pid}
    fi
}

## 重启脚本
restart(){
  stop
  start
}

case "$1" in
    status )
        status
        ;;
    start)
        start
        ;;
   stop)
        stop
        ;;
    *)
    echo "Usage: $0 {start|stop|restart|status} + serviceName"
    echo "例子:启动服务 ./server.sh  start $0"
    echo "例子:停止服务 ./server.sh  stop $0"
    echo "例子:重启服务 ./server.sh  restart $0"
    exit 2
esac