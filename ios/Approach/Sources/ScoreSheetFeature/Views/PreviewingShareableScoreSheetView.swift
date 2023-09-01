import AssetsLibrary
import ModelsLibrary
import ScoringServiceInterface
import SwiftUI
import SwiftUIExtensionsLibrary

public struct PreviewingShareableScoreSheetView: View {
	let style: ShareableScoreSheetConfiguration.Style
	private static let rolls = ["L", "/", "8"]

	@State private var contentSize: CGSize = .zero
	@State private var headerWidth: CGFloat = .zero
	@State private var rowHeight: CGFloat = .zero
	@State private var frameWidth: CGFloat = .zero

	public init(style: ShareableScoreSheetConfiguration.Style) {
		self.style = style
	}

	public var body: some View {
		Grid(alignment: .leading, horizontalSpacing: 0, verticalSpacing: 0) {
			GridRow {
				Text("")
					.frame(width: headerWidth)

				Text("1")
					.font(.caption2)
					.padding(.unitSpacing)
					.foregroundColor(style.textOnRail)
					.matchWidth(byKey: FrameWidthKey.self, to: $frameWidth)
					.frame(width: frameWidth > 0 ? frameWidth : nil)
					.background(style.railBackground)
					.roundCorners(topLeading: true)
			}

			GridRow {
				Text("Game 1")
					.font(.caption)
					.minimumScaleFactor(0.2)
					.lineLimit(1)
					.foregroundColor(style.textOnBackground)
					.padding(.smallSpacing)
					.matchWidth(byKey: GameHeaderWidthKey.self, to: $headerWidth)
					.matchHeight(byKey: RowHeightKey.self, to: $rowHeight)
					.frame(
						width: headerWidth > 0 ? headerWidth : nil,
						height: rowHeight > 0 ? rowHeight : nil
					)
					.background(style.background)
					.roundCorners(topLeading: true)

				Grid(horizontalSpacing: 0, verticalSpacing: 0) {
					GridRow {
						ForEach(Self.rolls, id: \.self) { roll in
							Text(roll)
								.font(.caption2)
								.minimumScaleFactor(0.2)
								.lineLimit(1)
								.padding(.unitSpacing)
								.foregroundColor(style.textOnBackground)
								.borders(
									trailing: true,
									bottom: true,
									color: style.border
								)
						}
					}

					GridRow {
						Text("23")
							.font(.caption)
							.padding(.smallSpacing)
							.foregroundColor(style.textOnBackground)
							.gridCellColumns(Frame.NUMBER_OF_ROLLS)
					}
				}
				.frame(height: rowHeight > 0 ? rowHeight : nil)
				.matchHeight(byKey: RowHeightKey.self, to: $rowHeight)
				.matchWidth(byKey: FrameWidthKey.self, to: $frameWidth)
				.background(style.background)
			}
		}
		.dynamicTypeSize(.medium)
		.measure(key: ContentSizeKey.self, to: $contentSize)
		.borders(
			trailing: true,
			bottom: true,
			color: style.strongBorder,
			thickness: 2
		)
	}
}

private struct ContentSizeKey: PreferenceKey, CGSizePreferenceKey {}
private struct GameHeaderWidthKey: PreferenceKey, MatchWidthPreferenceKey {}
private struct FrameWidthKey: PreferenceKey, MatchWidthPreferenceKey {}
private struct RowHeightKey: PreferenceKey, MatchHeightPreferenceKey {}

#if DEBUG
struct PreviewingShareableScoreSheetPreviews: PreviewProvider {
	static var previews: some View {
		Grid(horizontalSpacing: .standardSpacing, verticalSpacing: .standardSpacing) {
			GridRow {
				PreviewingShareableScoreSheetView(style: .plain)
				PreviewingShareableScoreSheetView(style: .default)
				PreviewingShareableScoreSheetView(style: .pride)
			}

			GridRow {
				PreviewingShareableScoreSheetView(style: .default)
				PreviewingShareableScoreSheetView(style: .plain)
				PreviewingShareableScoreSheetView(style: .pride)
			}

			GridRow {
				PreviewingShareableScoreSheetView(style: .plain)
				PreviewingShareableScoreSheetView(style: .default)
				PreviewingShareableScoreSheetView(style: .pride)
			}
		}
		.padding()
	}
}
#endif
