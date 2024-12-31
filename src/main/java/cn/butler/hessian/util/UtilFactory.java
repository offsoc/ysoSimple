/* MIT License

Copyright (c) 2017 Moritz Bechler

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package cn.butler.hessian.util;

//import com.fr.third.v2.org.apache.xmlbeans.impl.jam.xml.JamXmlElements;

import java.util.Comparator;


/**
 * @author mbechler
 *
 */
public class UtilFactory {

    public static Object makeHashCodeTrigger(Object o1) throws Exception {
        return JDKUtil.makeMap(o1, o1);
    }


    public static Object makeEqualsTrigger(Object tgt, Object sameHash) throws Exception {
        return JDKUtil.makeMap(tgt, sameHash);
    }

    /**
     * 帆软ToString触发器
     * @param obj
     * @return
     * @throws Exception
     */
//    public static Object makeToStringTriggerUnstable(Object obj) throws Exception {
//        Map tHashMap1 = (Map) createWithoutConstructor(Class.forName("javax.swing.UIDefaults$TextAndMnemonicHashMap"));
//        Map tHashMap2 = (Map) createWithoutConstructor(Class.forName("javax.swing.UIDefaults$TextAndMnemonicHashMap"));
//        tHashMap1.put(obj, null);
//        tHashMap2.put(obj, null);
//        setFieldValue(tHashMap1, "loadFactor", 1);
//        setFieldValue(tHashMap2, "loadFactor", 1);
//        HashMap hashMap = new HashMap();
//        Class node = Class.forName("java.util.HashMap$Node");
//        Constructor constructor = node.getDeclaredConstructor(Integer.TYPE, Object.class, Object.class, node);
//        constructor.setAccessible(true);
//        Object node1 = constructor.newInstance(0, tHashMap1, null, null);
//        Object node2 = constructor.newInstance(0, tHashMap2, null, null);
//        Field key = node.getDeclaredField("key");
//        Field modifiers = Field.class.getDeclaredField(JamXmlElements.MODIFIERS);
//        modifiers.setAccessible(true);
//        modifiers.setInt(key, key.getModifiers() & (-17));
//        key.setAccessible(true);
//        key.set(node1, tHashMap1);
//        key.set(node2, tHashMap2);
//        Field size = HashMap.class.getDeclaredField("size");
//        size.setAccessible(true);
//        size.set(hashMap, 2);
//        Field table = HashMap.class.getDeclaredField("table");
//        table.setAccessible(true);
//        Object arr = Array.newInstance(node, 2);
//        Array.set(arr, 0, node1);
//        Array.set(arr, 1, node2);
//        table.set(hashMap, arr);
//        return hashMap;
//    }

    /**
     * 功能: 构造object#toString方法的触发器
     * @param obj
     * @return
     * @throws Exception
     */
    public static Object makeToStringTriggerStable(Object obj) throws Exception {
        return ToStringUtil.makeToStringTrigger(obj);
    }


    public static Object makeIteratorTrigger(Object it) throws Exception {
        return JDKUtil.makeIteratorTriggerNative(it);
    }

    /**
     * 功能: 使用TreeMap构造object#compareTo方法的触发器
     * @param tgt
     * @param cmp
     * @return
     * @throws Exception
     */
    public static Object makeComparatorTrigger(Object tgt, Comparator<?> cmp) throws Exception {
        return JDKUtil.makeTreeMap(tgt, cmp);
    }

    /**
     * 功能: 使用TreeSet构造object#compareTo方法的触发器
     * @param v1
     * @param v2
     * @return
     * @throws Exception
     */
    public static Object makeTigger(Object v1, Object v2) throws Exception {
        return JDKUtil.makeTreeSet(v1,v2);
    }
}
