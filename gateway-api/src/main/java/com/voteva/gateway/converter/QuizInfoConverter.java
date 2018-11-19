package com.voteva.gateway.converter;

import com.voteva.gateway.web.to.out.QuizInfo;
import com.voteva.quiz.grpc.model.v1.GTestResultInfo;

public class QuizInfoConverter {

    public static QuizInfo convert(GTestResultInfo resultInfo) {
        return new QuizInfo(
                CommonConverter.convert(resultInfo.getTestUid()),
                resultInfo.getPercent());
    }
}
