package io.therapistai.analysis.application;

import io.therapistai.analysis.domain.MessageAnalysis;

public interface MessageAnalysisService {

    MessageAnalysis analyze(AnalysisInput input);

}