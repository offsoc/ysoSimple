package cn.butler.payloads;


/**
 * @author mbechler
 *
 */
public interface ReleaseableObjectPayload<T> extends ObjectPayload<T> {

    void release( T obj ) throws Exception;
}
