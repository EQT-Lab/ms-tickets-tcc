package br.com.equatorial.genesys.converter;

import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class JsonNodeConverter implements AttributeConverter<JSONObject, String> {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public String convertToDatabaseColumn(JSONObject data) {
		return data.toString();
	}

	@Override
	public JSONObject convertToEntityAttribute(String inputString) {
		JSONObject r = new JSONObject();
		try {
			r = (JSONObject) objectMapper.readValue(inputString, JSONObject.class);
		} catch (Exception e) {

		}
		return r;
	}
}
