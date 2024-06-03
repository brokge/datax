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
import com.alibaba.datax.transformer.Transformer;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * 身份证号码校验transformer
 * @author
 *
 */
public class IdCardConvertTransformer extends Transformer {
    public IdCardConvertTransformer() {
        setTransformerName("dx_idcard_convert");
    }

    @Override
    public Record evaluate(Record record, Object... paras) {
        int columnIndex;
        try {
            //校验参数个数
            if (paras.length != 1)
                throw new RuntimeException("dx_idcard_convert paras must be 1");
            columnIndex = ((Integer) paras[0]).intValue();
        } catch (Exception e) {
            throw DataXException.asDataXException(TransformerErrorCode.TRANSFORMER_ILLEGAL_PARAMETER,
                    (new StringBuilder()).append("paras:").append(Arrays.asList(paras).toString()).append(" => ")
                            .append(e.getMessage()).toString());
        }
        Column column = record.getColumn(columnIndex);
        try {
            String oriValue = column.asString();
            //如果字段为空，跳过replace处理
            if(oriValue == null){
                return  record;
            }
            String newValue;
            boolean isIdCard = IdCardUtils.isValidCard(oriValue);
            if (isIdCard) {
                newValue = IdCardUtils.convertIdCard(oriValue);
                // 如果为空则说明不是 15 位的身份证号
                if(newValue!=null) {
                    record.setColumn(columnIndex,new StringColumn(newValue));
                }
            }
        } catch (Exception e) {
            throw DataXException.asDataXException(TransformerErrorCode.TRANSFORMER_RUN_EXCEPTION, e.getMessage(), e);
        }
        return record;
    }

}
