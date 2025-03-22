package com.shemuel.idempotent.handler;

@FunctionalInterface
interface Handler<T, R> {

    R handle(T context) throws Exception;

}
