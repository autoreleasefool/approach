import AssetsLibrary
import DateTimeLibrary
import ModelsLibrary
import ModelsViewsLibrary
import ScoreSheetLibrary
import StringsLibrary
import SwiftUI

public struct ShareableGamesImage: View {
	@Environment(\.colorScheme) var colorScheme

	public let configuration: Configuration

	public init(configuration: Configuration) {
		self.configuration = configuration
	}

	public var body: some View {
		VStack(spacing: 0) {
			ForEach(configuration.scores) { game in
				ScoreSheet(game: game, selection: .constant(.none))
					.aspectRatio(5.0 / 9.0, contentMode: .fit)
					.frame(minWidth: 900)

			}
		}
		.frame(minWidth: 900, minHeight: 100)
	}
}

extension ShareableGamesImage {
	public struct Configuration: Equatable {
		public let scores: [ScoredGame]
		public let displayScale: CGFloat
		public let colorScheme: ColorScheme

		public init(scores: [ScoredGame], displayScale: CGFloat, colorScheme: ColorScheme) {
			self.scores = scores
			self.displayScale = displayScale
			self.colorScheme = colorScheme
		}
	}
}
