import Charts
import Foundation

public protocol GraphableStatistic {
	var plottable: any Plottable { get }
	mutating func accumulate(by: Self)
}
