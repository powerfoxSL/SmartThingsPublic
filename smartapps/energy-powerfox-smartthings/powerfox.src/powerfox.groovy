/**
 *  powerfox
 *
 *  Copyright 2021 Sven Lehmann
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */

definition(
    name: "powerfox",
    namespace: "energy.powerfox.smartthings",
    author: "Sven Lehmann",
    description: "Connects the powerfox sensor called poweropti to SmartThings. See more here https://shop.powerfox.energy/",
    category: "Green Living",
    iconUrl: "https://backend.powerfox.energy/content/images/powerfox_icon_60.png",
    iconX2Url: "https://backend.powerfox.energy/content/images/powerfox_icon_120.png",
    iconX3Url: "https://backend.powerfox.energy/content/images/powerfox_icon_180.png")

preferences {
    section("Configure your poweropti") {
        input "poweroptiID", "text", title: "poweropti ID", required: true
        input "emailAddress", "text", title: "E-Mail Address (powerfox Account)", required: true
        input "password", "password", title: "Password (powerfox Password)", required: true
    }
}

def installed() {
	log.debug "Installed with settings: ${settings.poweroptiID} and ${settings.emailAddress}"

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings.poweroptiID} and ${settings.emailAddress}"

	unsubscribe()
	initialize()
}

def initialize() {
	schedule("* * * * * ?", updateCurrentPower)
}

def updateCurrentPower() {
	def authEncoded = "${emailAddress}:${password}".bytes.encodeBase64()
    def current = 0
	
    def params = [
        uri: "https://backend.powerfox.energy",
        path: "/api/2.0/my/${poweroptiID}/current",
		contentType: 'application/json',
		headers: [
			"Authorization" : "Basic ${authEncoded}"
		]
    ]
    
    try {
        httpGet(params) { resp ->   
            log.debug "response data: ${resp.data}"
            current = resp.data.Watt
            log.debug "current: ${current} Watts"    
        }
    } catch (e) {
        log.error "something went wrong: $e"
    }
}