/*
 * Copyright 2011-2021 GatlingCorp (https://gatling.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package computerdatabase

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class BasicSimulationWrite3000tps extends Simulation {

  val httpProtocol = http
		.baseUrl("http://localhost:8080")
		.inferHtmlResources(BlackList(""".*\.js""", """.*\.css""", """.*\.gif""", """.*\.jpeg""", """.*\.jpg""", """.*\.ico""", """.*\.woff""", """.*\.woff2""", """.*\.(t|o)tf""", """.*\.png""", """.*detectportal\.firefox\.com.*"""), WhiteList())
		.acceptHeader("application/json, text/plain, */*")
		.acceptEncodingHeader("gzip, deflate")
		.acceptLanguageHeader("es-ES,es;q=0.8,en-US;q=0.5,en;q=0.3")
		.userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.13; rv:68.0) Gecko/20100101 Firefox/68.0")

	val headers_1 = Map(
		"Accept" -> "application/json",
		"Content-Type" -> "application/json; charset=utf-8",
		"Origin" -> "https://authenticatorweb-staging.bancodebogota.com.co",
		"aditional-Info" -> "private-ip=null;",
		"channel" -> "WEB",
		"product" -> "3")
  
  var myVar = 1;

	val scn = scenario("/v1/transactions 1000 tps")
    .exec(session => {
        myVar += 1
        session.set("myVar", myVar) // 2, 3, 4...
    })
		.exec(
			http("get-info")
			.post("/v1/transactions")
			.headers(headers_1)
			.body((StringBody("""{
  "amount": 1.1,
  "currency": "USD",
  "origin_account": "${myVar}",
  "destination_account": "1",
  "description": "hello"
}"""))
		)
  )

	setUp(scn.inject(
		constantUsersPerSec(1000) during (1 minutes)
	)).protocols(httpProtocol)
}
