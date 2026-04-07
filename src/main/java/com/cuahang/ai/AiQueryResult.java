package com.cuahang.ai;

import java.util.List;

public record AiQueryResult(List<String> columns, List<List<Object>> rows) {
}

