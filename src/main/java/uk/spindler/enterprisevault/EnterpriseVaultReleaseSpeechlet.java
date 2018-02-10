/**
Copyright 2014-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance with the License. A copy of the License is located at

    http://aws.amazon.com/apache2.0/

or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/
package uk.spindler.enterprisevault;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.SpeechletV2;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import com.amazon.speech.ui.OutputSpeech;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;




/**
* This sample shows how to create a simple speechlet for handling speechlet requests.
*/
public class EnterpriseVaultReleaseSpeechlet implements SpeechletV2{
private static final Logger log = LoggerFactory.getLogger(EnterpriseVaultReleaseSpeechlet.class);


private HashMap<String, String> map;

//Strings
private static String RESPONSE_DATA_AVAILABLE 			= 	"The Enterprise Vault release information could not be read from WikiPedia, please try again later";
private static String RESPONSE_NOT_AVAILABLE 			= 	"The Enterprise Vault release date could not be determined at this time, please try again later";
private static String RESPONSE_HELP 					= 	"This skill allows you to ask about Enterprise Vault release dates, you can say,  When was Version Nine released?, or, when did version nine service pack three ship? ";
private static String RELEASE_CARD_NAME 				=	"Enterprise Vault Release Information";
private static String WELCOME_RESPONSE 					= 	"Welcome to the Enterprise Vault Release Information Skill, you can ask when a specific Enterprise Vault version was released";
private static String UNSUPPORTED_REQUEST 	   		 	=   "This is unsupported.  Please try something else";
private static String ENTERPRISE_VAUL_WIKIPEDIA_URL 	=   "https://en.wikipedia.org/wiki/Enterprise_Vault";
private static String RESPONSE_STOP 					=   "Thanks for using the Enterprise Vault Release Information skill, goodbye";
private static String RESPONSE_CANCEL					=   "Thanks for using the Enterprise Vault Release Information skill, goodbye";
private static String REQUEST_VERSION_INFORMATION       =   "Which release version are you interested in?";


@Override

public void onSessionStarted(SpeechletRequestEnvelope<SessionStartedRequest> requestEnvelope) {
    log.info("onSessionStarted requestId={}, sessionId={}", requestEnvelope.getRequest().getRequestId(),
            requestEnvelope.getSession().getSessionId());

}

@Override
public SpeechletResponse onLaunch(SpeechletRequestEnvelope<LaunchRequest> requestEnvelope) {
    log.info("onLaunch requestId={}, sessionId={}", requestEnvelope.getRequest().getRequestId(),
            requestEnvelope.getSession().getSessionId());
    return getWelcomeResponse();
}

@Override
public SpeechletResponse onIntent(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
    IntentRequest request = requestEnvelope.getRequest();
    log.info("onIntent requestId={}, sessionId={}", request.getRequestId(),
            requestEnvelope.getSession().getSessionId());
    
    BasicConfigurator.configure();
    //Extract the parameters passed in the intent message
    Intent intent = request.getIntent();
    String intentName = (intent != null) ? intent.getName() : null;
    

    //Load the EV Release data from WikiPedia
    SpeechletResponse response = null;
   
	switch(intentName)
	{
		case "ReleaseDateIntent":
			{
				String major = (intent != null) ? intent.getSlot("version").getValue() : null;
			    String minor = (intent != null) ? intent.getSlot("minor").getValue() : null;
			    String build = (intent != null) ? intent.getSlot("build").getValue() : null;
			    String servicepack = (intent != null) ? intent.getSlot("servicepack").getValue() : null;
	    	
			    // Check for a partial intent - i.e. no version specified
			    if(StringUtils.isNotEmpty(major)) {
			    	try {
			    		map = GetEVReleaseDataFromWikiPedia();
			    		
			    		if(map.size() == 0) 
			    	    	 response= getSpeechletResponse(RELEASE_CARD_NAME, RESPONSE_DATA_AVAILABLE, "", false);
			    	    else	 
			    	    	response = getReleaseDateResponse(major, minor, build, servicepack);
					} catch (Exception e) {
						e.printStackTrace();
					}
			    }
			    else
			    {
			    	// Partial Intent - ask for additional version information and optionally the service pack number
			    	response =  getSpeechletResponse(RELEASE_CARD_NAME, "", REQUEST_VERSION_INFORMATION, true);
			    }
			}
			break;
		case "AMAZON.StopIntent":
			response = getStopResponse();
			break;
		case "AMAZON.CancelIntent":
			response = getCancelResponse();
			break;
		case "AMAZON.HelpIntent":
		
			response = getHelpResponse();
			break;
		default:
			response = getSpeechletResponse(RELEASE_CARD_NAME, UNSUPPORTED_REQUEST, "", false);
			break;
	};
    
    return response;
}


@Override
public void onSessionEnded(SpeechletRequestEnvelope<SessionEndedRequest> requestEnvelope) {
    log.info("onSessionEnded requestId={}, sessionId={}", requestEnvelope.getRequest().getRequestId(),
            requestEnvelope.getSession().getSessionId());
    // any cleanup logic goes here
}

/**
 * Creates and returns a {@code SpeechletResponse} with a welcome message.
 *
 * @return SpeechletResponse spoken and visual response for the given intent
 */
private SpeechletResponse getWelcomeResponse() {
    return getSpeechletResponse(RELEASE_CARD_NAME, WELCOME_RESPONSE, "", true);
}

private SpeechletResponse getReleaseDateResponse(String major, String minor, String build, String servicepack) {
   
	String speechText = RESPONSE_NOT_AVAILABLE;
	
	try {
		speechText = getReleaseDateText(major, minor, build, servicepack);
	} catch (Exception e) {
		e.printStackTrace();
	}
   
    // Create the Simple card content.
    SimpleCard card = getSimpleCard(RELEASE_CARD_NAME, speechText);

    // Create the plain text output.
    PlainTextOutputSpeech speech = getPlainTextOutputSpeech(speechText);

    return SpeechletResponse.newTellResponse(speech, card); 
}

private  String getReleaseDateText(String major, String minor, String build, String servicePack) throws Exception {
	String result = RESPONSE_NOT_AVAILABLE;
	VersionInfo vi = new VersionInfo(major, minor, build, servicePack);
	
    log.info("lookup mapKey: " + vi.getMapKey());
    
	String  datestring  = map.get(vi.getMapKey());
	
	if(StringUtils.isNotEmpty(datestring))
	{
		result = String.format(vi.getVersionSpeechText(), datestring);
	}
	else
		result = RESPONSE_NOT_AVAILABLE;
	
	
	return result;
}

private  HashMap<String, String> GetEVReleaseDataFromWikiPedia()
{
	HashMap<String, String> map = new HashMap<String, String>();
	final Pattern pattern = Pattern.compile("<li>Enterprise Vault\\s*(\\d*\\.?\\d+?\\.?\\d+?\\s*(SP\\d)?)(.*released\\s*(on|in)\\s*(\\d{2}\\s*\\w{1,9}\\s*\\d{4}))");
	 
	
	 try {
			Document doc = Jsoup.connect(ENTERPRISE_VAUL_WIKIPEDIA_URL).get();
			// Diginto the response HTML to access the Enterprise Vault release information
			Element masthead = doc.select("div.mw-parser-output").first();
			Elements links = masthead.getElementsByTag("ul");
			
			for (Element e : links) {
				//System.out.print(e.html());
				if(e.html().toLowerCase().contains("Enterprise Vault".toLowerCase()) &&  e.html().toLowerCase().contains("release".toLowerCase()))
				{
					String line = e.html();
					
					Matcher matcher = pattern.matcher(line);
					while (matcher.find())
					{
						log.info(matcher.group(1));
						VersionInfo vi = new VersionInfo(matcher.group(1));	
						
						log.info("Inserted mapKey: " + vi.getMapKey() + " Value: " + matcher.group(5));
						map.put(vi.getMapKey(), matcher.group(5));
					}
				}
			}
	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	return map;
}

/**
 * Creates a {@code SpeechletResponse} for the help intent.
 *
 * @return SpeechletResponse spoken and visual response for the given intent
 */
private SpeechletResponse getStopResponse() {
    String speechText = RESPONSE_STOP;
    return getSpeechletResponse(RELEASE_CARD_NAME, speechText, "", false);
}

 private SpeechletResponse getCancelResponse() {
        String speechText = RESPONSE_CANCEL;
        return getSpeechletResponse(RELEASE_CARD_NAME, speechText, "", false);
 }


/**
 * Creates a {@code SpeechletResponse} for the help intent.
 *
 * @return SpeechletResponse spoken and visual response for the given intent
 */
private SpeechletResponse getHelpResponse() {
    String speechText = RESPONSE_HELP;
    return getSpeechletResponse(RELEASE_CARD_NAME, speechText, REQUEST_VERSION_INFORMATION, true);
}

/**
 * Helper method that creates a card object.
 * @param title title of the card
 * @param content body of the card
 * @return SimpleCard the display card to be sent along with the voice response.
 */
private SimpleCard getSimpleCard(String title, String content) {
    SimpleCard card = new SimpleCard();
    card.setTitle(title);
    card.setContent(content);

    return card;
}

/**
 * Helper method for retrieving an OutputSpeech object when given a string of TTS.
 * @param speechText the text that should be spoken out to the user.
 * @return an instance of SpeechOutput.
 */
private PlainTextOutputSpeech getPlainTextOutputSpeech(String speechText) {
    PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
    speech.setText(speechText);

    return speech;
}



/**
 * Helper method that returns a reprompt object. This is used in Ask responses where you want
 * the user to be able to respond to your speech.
 * @param outputSpeech The OutputSpeech object that will be said once and repeated if necessary.
 * @return Reprompt instance.
 */
private Reprompt getReprompt(OutputSpeech outputSpeech) {
    Reprompt reprompt = new Reprompt();
    reprompt.setOutputSpeech(outputSpeech);

    return reprompt;
}

/**
 * Returns a Speechlet response for a speech and reprompt text.
 */
private SpeechletResponse getSpeechletResponse(String cardTitle, String speechText, String repromptText, boolean isAskResponse) {
    // Create the Simple card content.
    SimpleCard card = new SimpleCard();
    card.setTitle(cardTitle);
    card.setContent(speechText);

    // Create the plain text output.
    PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
    speech.setText(speechText);

    if (isAskResponse) {
        // Create reprompt
        PlainTextOutputSpeech repromptSpeech = new PlainTextOutputSpeech();
        repromptSpeech.setText(repromptText);
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(repromptSpeech);

        return SpeechletResponse.newAskResponse(speech, reprompt, card);

    } else {
        return SpeechletResponse.newTellResponse(speech, card);
    }
}



}