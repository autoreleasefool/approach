import StatisticsLibrary
import StringsLibrary
import XCTest

func AssertCounting<T>(
	_ statistic: T,
	equals count: Int,
	file: StaticString = #file,
	line: UInt = #line
) where T: Statistic & CountingStatistic {
	XCTAssertEqual(statistic.count, count, file: file, line: line)
	XCTAssertEqual(statistic.formattedValue, String(count), file: file, line: line)
	XCTAssertEqual(statistic.isEmpty, count == 0, file: file, line: line)
}

func AssertHighestOf<T>(
	_ statistic: T,
	equals highest: Int,
	file: StaticString = #file,
	line: UInt = #line
) where T: Statistic & HighestOfStatistic {
	XCTAssertEqual(statistic.highest, highest, file: file, line: line)
	XCTAssertEqual(statistic.formattedValue, String(highest), file: file, line: line)
	XCTAssertEqual(statistic.isEmpty, highest == 0, file: file, line: line)
}

func AssertAveraging<T>(
	_ statistic: T,
	hasTotal total: Int,
	withDivisor divisor: Int,
	file: StaticString = #file,
	line: UInt = #line
) where T: Statistic & AveragingStatistic {
	let average = divisor > 0 ? Double(total) / Double(divisor) : 0
	XCTAssertEqual(statistic.total, total, file: file, line: line)
	XCTAssertEqual(statistic.divisor, divisor, file: file, line: line)
	XCTAssertEqual(statistic.isEmpty, divisor == 0, file: file, line: line)
	XCTAssertEqual(statistic.average, average, file: file, line: line)
	XCTAssertEqual(statistic.formattedValue, format(average: average), file: file, line: line)
}
