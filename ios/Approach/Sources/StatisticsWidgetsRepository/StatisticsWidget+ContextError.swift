import Foundation
import ModelsLibrary

extension StatisticsWidget {
	public enum ContextError: Error, LocalizedError {
		case mismatchedContexts

		public var errorDescription: String? {
			switch self {
			case .mismatchedContexts: return "Contexts for all widgets do not match."
			}
		}
	}
}
