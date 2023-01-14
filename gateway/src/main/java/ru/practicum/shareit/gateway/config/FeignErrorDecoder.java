package ru.practicum.shareit.gateway.config;

import feign.Response;
import feign.codec.ErrorDecoder;
import ru.practicum.shareit.gateway.exception.EntityNotFoundException;
import ru.practicum.shareit.gateway.exception.ForbiddenException;
import ru.practicum.shareit.gateway.exception.BadRequestException;

public class FeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder errorDecoder = new Default();

    @Override
    public Exception decode(String s, Response response) {

       /* ExceptionMessage message = null;
        try (InputStream bodyIs = response.body().asInputStream()) {
            ObjectMapper mapper = new ObjectMapper();
            message = mapper.readValue(bodyIs, ExceptionMessage.class);
        } catch (IOException e) {
            return new Exception(e.getMessage());
        }*/
        switch (response.status()) {
            case 400:
                return new ForbiddenException( "Bad Request error from server");
            case 404:
                return new EntityNotFoundException("Not found error from server");
            case 403:
                return new BadRequestException("Forbidden error from server");
            default:
                return new Exception("Generic exception from server");
        }
    }

}
