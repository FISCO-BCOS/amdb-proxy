#!/bin/bash

nohup setsid java -cp "conf/:apps/*:lib/*" org.bcos.amdb.server.Main &
