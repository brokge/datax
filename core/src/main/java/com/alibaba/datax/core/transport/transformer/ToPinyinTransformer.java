package com.alibaba.datax.core.transport.transformer;

/**
 * @author chenlw
 * @date 2024/5/23 11:12
 */

import com.alibaba.datax.common.element.Column;
import com.alibaba.datax.common.element.Record;
import com.alibaba.datax.common.element.StringColumn;
import com.alibaba.datax.common.exception.DataXException;
import com.alibaba.datax.core.util.IdCardUtils;
import com.alibaba.datax.core.util.PinyinUtil;
import com.alibaba.datax.transformer.Transformer;

import java.util.Arrays;

/**
 * 拼音
 *
 * @author
 */
public class ToPinyinTransformer extends Transformer {

    public ToPinyinTransformer() {
        setTransformerName("dx_to_pinyin");
    }


    @Override
    public Record evaluate(Record record, Object... paras) {
        int columnIndex;
        // UPPERCASE LOWERCASE
        String caseType ="UPPERCASE";
        // " "
        String separate=" ";
        // 1
        String sorted = "1";
        try {
            //校验参数个数
            if (paras.length != 4) {
                throw new RuntimeException("dx_to_pinyin paras must be 4");
            }
            columnIndex = (Integer) paras[0];
            caseType = (String) paras[1];
            separate = (String) paras[2];
            sorted = (String) paras[3];
        } catch (Exception e) {
            throw DataXException.asDataXException(TransformerErrorCode.TRANSFORMER_ILLEGAL_PARAMETER,
                    (new StringBuilder()).append("paras:").append(Arrays.asList(paras).toString()).append(" => ")
                            .append(e.getMessage()).toString());
        }
        Column column = record.getColumn(columnIndex);
        String inputValue = column.asString();

        try {
            String newValue = PinyinUtil.toPinyin(inputValue, caseType, separate, sorted);
            if (newValue != null) {
                // 字符串转拼音
                record.setColumn(columnIndex, new StringColumn(newValue));
            }
        } catch (Exception e) {
            throw DataXException.asDataXException(TransformerErrorCode.TRANSFORMER_RUN_EXCEPTION, e.getMessage(), e);
        }
        return record;
    }

}
