#!/bin/bash

if [[ "$TRAVIS_JDK_VERSION" = "*7*" ]]; then
  exec sbt clean compile test
fi

if [[ "$TRAVIS_JDK_VERSION" = "*8*" ]]; then
  exec sbt clean compile tut coverage test && sbt coverageAggregate
fi