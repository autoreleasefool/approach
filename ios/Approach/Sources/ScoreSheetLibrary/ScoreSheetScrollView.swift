import AssetsLibrary
import ModelsLibrary
import SwiftUI
import SwiftUIExtensionsPackageLibrary

public struct ScoreSheetScrollView: View {
	let game: ScoredGame
	let configuration: ScoreSheet.Configuration
	@State var contentSize: CGSize = .zero
	@Binding var selection: ScoreSheet.Selection

	public init(game: ScoredGame, configuration: ScoreSheet.Configuration, selection: Binding<ScoreSheet.Selection>) {
		self.game = game
		self.configuration = configuration
		self._selection = selection
	}

	public var body: some View {
		ScrollViewReader { proxy in
			ScrollView(.horizontal, showsIndicators: false) {
				ScoreSheet(game: game, configuration: configuration, contentSize: contentSize, selection: $selection)
					.cornerRadius(.standardRadius)
			}
			.onChange(of: selection) {
				withAnimation(.easeInOut(duration: 300)) {
					proxy.scrollTo(selection.frameId, anchor: selection.isLast ? .trailing : .leading)
				}
			}
			.onAppear {
				withAnimation(.easeInOut(duration: 300)) {
					proxy.scrollTo(selection.frameId, anchor: selection.isLast ? .trailing : .leading)
				}
			}
			.measure(key: ContentSizeKey.self, to: $contentSize)
		}
	}
}

private struct ContentSizeKey: PreferenceKey, CGSizePreferenceKey {}
