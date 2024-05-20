import Foundation
import ModelsLibrary

public enum SeriesListError: Error, LocalizedError {
	case seriesNotFound(Series.ID)

	public var errorDescription: String? {
		switch self {
		case let .seriesNotFound(id):
			return "Could not find Series with ID '\(id)'"
		}
	}
}
