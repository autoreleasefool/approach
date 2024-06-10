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
				ScoreSheet(
					game: game,
					configuration: configuration.scoreSheetConfiguration,
					contentSize: CGSize(width: 360, height: 100),
					selection: .constant(.none)
				)
			}
		}
	}
}

extension ShareableGamesImage {
	public struct Configuration: Equatable {
		public let scores: [ScoredGame]
		public let scoreSheetConfiguration: ScoreSheet.Configuration
		public let displayScale: CGFloat
		public let colorScheme: ColorScheme

		public init(
			scores: [ScoredGame],
			scoreSheetConfiguration: ScoreSheet.Configuration,
			displayScale: CGFloat,
			colorScheme: ColorScheme
		) {
			self.scores = scores
			self.scoreSheetConfiguration = scoreSheetConfiguration
			self.displayScale = displayScale
			self.colorScheme = colorScheme
		}
	}
}

#Preview(traits: .sizeThatFitsLayout) {
	ShareableGamesImage(configuration: ShareableGamesImage.Configuration(
		scores: (0...4).map {
			ScoredGame(
				id: UUID(uuidString: "00000000-0000-0000-0000-00000000000\($0)")!,
				index: $0,
				frames: Game.FRAME_INDICES.map {
					.init(
						index: $0,
						rolls: [
							.init(index: 0, displayValue: "HP", didFoul: true, isSecondary: false),
							.init(index: 1, displayValue: "10", didFoul: false, isSecondary: false),
							.init(index: 2, displayValue: "-", didFoul: false, isSecondary: false),
						],
						score: 255
					)
				}
			)
		},
		scoreSheetConfiguration: .default,
		displayScale: .zero,
		colorScheme: .light
	))
}
