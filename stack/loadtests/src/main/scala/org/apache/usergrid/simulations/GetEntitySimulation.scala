/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 package org.apache.usergrid.simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
 import org.apache.usergrid.datagenerators.FeederGenerator
 import org.apache.usergrid.scenarios.{EntityScenarios, TokenScenarios}
 import org.apache.usergrid.settings.{Utils, Headers, Settings}


class GetEntitySimulation extends Simulation {

  // Target settings
  val httpConf = Settings.httpConf
  if(!Settings.skipSetup) {
    println("Begin setup")
    exec(TokenScenarios.getManagementToken)
    println("End Setup")
  }else{
    println("Skipping Setup")
  }

  // Simulation settings
  val numUsers:Int = Settings.numUsers
  val numEntities:Int = Settings.numEntities
  val rampTime:Int = Settings.rampTime
  val throttle:Int = Settings.throttle

  val feeder = FeederGenerator.generateCustomQuery(0)

    // Creates a scenario where the feeder generates Custom GET queries. Forever ensures that the scenario is run continuously.
    val scnToRun = scenario("Get entity")
        .forever(
        feed(feeder)
        .exec(EntityScenarios.getRecommendation))

    // Injects maxPossible users at once in the start and then each user keeps running the scnToRun for the duration specified
    setUp(scnToRun.inject(atOnceUsers(Settings.maxPossibleUsers)
  ).protocols(httpConf)).maxDuration(Settings.duration)
}
