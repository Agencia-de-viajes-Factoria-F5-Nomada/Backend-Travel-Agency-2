package com.inditex.gym_lorza.exception;

public class ObjectNotFoundException extends RuntimeException {
    public ObjectNotFoundException(String objectName, Long id) {
        super("No hemos podido encontrar en la BBDD la información la "+ objectName + ", con el id:"+id);
    }
}
