// package com.ds.feige.im.test.nlp;
//
// import java.util.List;
//
// import com.hankcs.hanlp.HanLP;
// import com.hankcs.hanlp.model.crf.CRFLexicalAnalyzer;
// import com.hankcs.hanlp.model.crf.CRFSegmenter;
// import com.hankcs.hanlp.seg.Segment;
// import com.hankcs.hanlp.seg.common.Term;
// import com.hankcs.hanlp.tokenizer.NLPTokenizer;
//
/// **
// * @author DC
// */
// public class HLPTest {
// public static void main(String[] args) throws Exception {
// long start = System.currentTimeMillis();
// String text = "文本相似度的比较在很多场景都可以用到，比如之前CMB的客户地址比较，比如搜索，比如机器人问答…\n"
// + "Python平台的各种解决方案都已经很成熟，算出来的结果也很令人满意，但是用Python做好之后，由于臭名昭著的GIL(Global Interpreter Lock)，由于缓慢的Looping…
// 等等原因都令得它的部署很昂贵，要很好的机器才能满足高并发的需求。\n"
// + "山哥留意到Java平台有个叫DL4J的，在NLP方面也很成熟，各种神经网络，CPU，GPU
// CUDA都支持，测评结果说，比Tensorflow的性能还好些。于是山哥心动了。毕竟Java的性能是钢钢的！尤其是用过JVisualVm来监控调优性能之后，山哥已经不相信现阶段有比JVM更适合做后台服务的平台了。\n"
// + "在网上搜索了一下例子， 发现DL4J没有一个很好的做文本相似度比较的例子，有的都是利用它算Word2Vec，然后找单词相似度…呃，这个很好，但是离应用还差十八万千里啊！得！还是得自己动手！\n"
// + "借鉴了Python的版本，看了各种NLP的论文资料，还是把Solution定位如此：\n" + "\n" + "用TF-IDF训练语料库\n"
// + "基于这个TF-IDF模型，计算目标Text的向量，和源Text的向量\n" + "计算向量余弦相似度（Cosine Similarity）\n"
// +
// "TF-IDF挺神奇的，它不需要神经网络，但是在某些场景效果特别好，所以据说Google的搜索算法也用它。而且由于不用做神经网络的大量计算，用CPU就够了。真是“又便宜又好，我们一直都拿它当宝！大哥买了送大嫂，大嫂高兴的不得了…”咳咳！\n"
// + "好了，废话一堆，永远比不上几句代码直接。来吧……\n" + "\n" + "作者：山哥Samuel\n" + "链接：https://www.jianshu.com/p/70864d1c278b\n"
// + "来源：简书\n" + "著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。";
// List<Term> terms1 = NLPTokenizer.segment(text);
// terms1.forEach(term -> {
// System.out.println(term);
// });
// long end = System.currentTimeMillis();
// System.out.println("耗时:" + (end - start));
// HanLP.Config.ShowTermNature = false; // 关闭词性显示
// Segment segment = new CRFLexicalAnalyzer(new CRFSegmenter());
// String[] sentenceArray = new String[] {text};
// for (String sentence : sentenceArray) {
// List<Term> termList = segment.seg(sentence);
// System.out.println(termList);
// }
// }
// }
