import Foundation

public let averageFormatter: NumberFormatter = {
	let formatter = NumberFormatter()
	formatter.maximumFractionDigits = 1
	formatter.alwaysShowsDecimalSeparator = false
	return formatter
}()

public func format(average: Double?) -> String {
	guard let average, average > 0 else { return "—" }
	return averageFormatter.string(from: NSNumber(value: average)) ?? ""
}
