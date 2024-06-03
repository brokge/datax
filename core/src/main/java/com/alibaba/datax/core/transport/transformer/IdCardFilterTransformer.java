package com.alibaba.datax.core.transport.transformer;

/**
 * @author chenlw
 * @date 2024/5/23 11:12
 */
import com.alibaba.datax.common.element.Column;
import com.alibaba.datax.common.element.Record;
import com.alibaba.datax.common.exception.DataXException;
import com.alibaba.datax.core.util.IdCardUtils;
import com.alibaba.datax.transformer.Transformer;

import java.util.Arrays;

/**
 * 身份证号码校验transformer
 * @author
 *
 */
public class IdCardFilterTransformer extends Transformer {
    public IdCardFilterTransformer() {
        setTransformerName("dx_idcard_filter");
    }

    @Override
    public Record evaluate(Record record, Object... paras) {
        int columnIndex;
        try {
            //校验参数个数
            if (paras.length != 1)
                throw new RuntimeException("dx_idcard_Filter paras must be 1");
            columnIndex = ((Integer) paras[0]).intValue();

        } catch (Exception e) {
            throw DataXException.asDataXException(TransformerErrorCode.TRANSFORMER_ILLEGAL_PARAMETER,
                    (new StringBuilder()).append("paras:").append(Arrays.asList(paras).toString()).append(" => ")
                            .append(e.getMessage()).toString());
        }
        Column column = record.getColumn(columnIndex);
        String idCard = column.asString();
        try {
            boolean isIdCard = IdCardUtils.isValidCard(idCard);
            if (!isIdCard) {
                return null;
            }
        } catch (Exception e) {
            throw DataXException.asDataXException(TransformerErrorCode.TRANSFORMER_RUN_EXCEPTION, e.getMessage(), e);
        }
        return record;
    }

}
