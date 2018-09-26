#!/bin/bash
    weth_pid=`ps aux|grep "amdb.server.Main"|grep -v grep|awk '{print $2}'`
    kill_cmd="kill -9 ${weth_pid}"
    eval ${kill_cmd}
