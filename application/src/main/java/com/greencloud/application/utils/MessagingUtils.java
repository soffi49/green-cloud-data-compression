package com.greencloud.application.utils;

import static com.google.common.collect.Collections2.filter;
import static com.greencloud.application.mapper.JsonMapper.getMapper;

import java.util.Collection;
import java.util.Vector;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.greencloud.application.exception.IncorrectMessageContentException;

import jade.lang.acl.ACLMessage;

/**
 * Class defines set of utilities used for message exchange
 */
public class MessagingUtils {

	/**
	 * Method retrieves messages from all responses, the ones which match the given performative
	 *
	 * @param responses    all responses
	 * @param performative performative to filter by
	 * @return responses matching the performative
	 */
	public static Collection<ACLMessage> retrieveForPerformative(Vector<ACLMessage> responses, Integer performative) {
		return filter(responses, response -> response.getPerformative() == performative);
	}

	/**
	 * Method reads the message object content
	 *
	 * @param message           messages to read
	 * @param expectedClassType class type of the message body
	 * @return mapped to Object message content
	 */
	public static <T> T readMessageContent(final ACLMessage message, final Class<T> expectedClassType) {
		try {
			return getMapper().readValue(message.getContent(), expectedClassType);
		} catch (MismatchedInputException | JsonParseException e) {
			throw new IncorrectMessageContentException();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new IncorrectMessageContentException();
		}
	}

	/**
	 * Method verifies if content of given message is correct
	 *
	 * @param message      received message
	 * @param expectedType expected content type
	 * @return boolean indicating if content has valid type
	 */
	public static boolean isMessageContentValid(final ACLMessage message, final Class<?> expectedType) {
		try {
			getMapper().readValue(message.getContent(), expectedType);
			return true;
		} catch (JsonProcessingException e) {
			return false;
		}
	}
}
