package cn.netdiscovery.core.queue.filter;

import cn.netdiscovery.core.domain.Request;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by tony on 2018/1/3.
 */
public class BloomDuplicateFilter implements DuplicateFilter {

    private int expectedInsertions;

    private double fpp;

    private AtomicInteger counter;

    private final BloomFilter<CharSequence> bloomFilter;

    public BloomDuplicateFilter(int expectedInsertions) {
        this(expectedInsertions, 0.01);
    }

    /**
     *
     * @param expectedInsertions the number of expected insertions to the constructed
     * @param fpp the desired false positive probability (must be positive and less than 1.0)
     */
    public BloomDuplicateFilter(int expectedInsertions, double fpp) {
        this.expectedInsertions = expectedInsertions;
        this.fpp = fpp;
        this.bloomFilter = rebuildBloomFilter();
    }

    protected BloomFilter<CharSequence> rebuildBloomFilter() {
        counter = new AtomicInteger(0);
        return BloomFilter.create(Funnels.stringFunnel(Charset.defaultCharset()), expectedInsertions, fpp);
    }


    @Override
    public boolean isDuplicate(Request request) {

        boolean isDuplicate = bloomFilter.mightContain(request.getUrl());
        if (!isDuplicate) {
            bloomFilter.put(request.getUrl());
            counter.incrementAndGet();
        }
        return isDuplicate;
    }

    @Override
    public int getTotalRequestsCount() {
        return counter.get();
    }
}
