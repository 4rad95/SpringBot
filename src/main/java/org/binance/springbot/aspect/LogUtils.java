package org.binance.springbot.aspect;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LogUtils {

    public static String getDaoResultLogInfo(final Logger log, final Object result) {
        StringBuilder resultInfo = new StringBuilder();

        if (result instanceof List) {
            resultInfo.append("RESULT_SIZE=").append(((List<?>) result).size());
            resultInfo.append(", { First record } : ");
            resultInfo.append(((List<?>)result).getFirst().toString());
        }

        if (log.isDebugEnabled() || !(result instanceof List)) {
            if (!resultInfo.isEmpty()) {
                resultInfo.append(" ");
            }
            resultInfo.append(result);
        }

        return resultInfo.toString();
    }
}