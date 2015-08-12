#!/bin/bash
export DISPLAY=:1
openscad -o /opt/tomcat/webapps/NameTag/images/x.png --camera=0,0,0,0,0,0,100 /opt/tomcat/webapps/NameTag/openscad/name.scad