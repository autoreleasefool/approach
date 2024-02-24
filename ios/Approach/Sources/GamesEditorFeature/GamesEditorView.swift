import AssetsLibrary
import AvatarServiceInterface
import ComposableArchitecture
import EquatableLibrary
import ErrorsFeature
import ExtensionsLibrary
import FeatureActionLibrary
import ModelsLibrary
import ResourcePickerLibrary
import ScoreSheetLibrary
import SharingFeature
import StoreKit
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary

@ViewAction(for: GamesEditor.self)
public struct GamesEditorView: View {
	@Perception.Bindable public var store: StoreOf<GamesEditor>

	@Environment(\.continuousClock) private var clock
	@Environment(\.safeAreaInsets) private var safeAreaInsets
	@Environment(\.requestReview) private var requestReview
	@State private var headerContentSize: CGSize = .zero
	@State private var rollEditorSize: CGSize = .zero
	@State private var frameContentSize: CGSize = .zero
	@State private var sheetContentSize: CGSize = .zero
	@State private var windowContentSize: CGSize = .zero

	public init(store: StoreOf<GamesEditor>) {
		self.store = store
	}

	public var body: some View {
		WithPerceptionTracking {
			VStack {
				GamesHeaderView(store: store.scope(state: \.gamesHeader, action: \.internal.gamesHeader))
					.measure(key: HeaderContentSizeKey.self, to: $headerContentSize)

				VStack {
					if let manualScore = store.manualScore {
						Spacer()

						VStack {
							Text(String(manualScore))
								.font(.largeTitle)
							Text(Strings.Game.Editor.Fields.ManualScore.caption)
								.font(.caption)
						}
						.padding()
						.background(.regularMaterial, in: RoundedRectangle(cornerRadius: .standardRadius, style: .continuous))
						.padding()

						Spacer()

					} else {

						Spacer()

						FrameEditorView(store: store.scope(state: \.frameEditor, action: \.internal.frameEditor))
							.padding(.top)

						Spacer()

						RollEditorView(store: store.scope(state: \.rollEditor, action: \.internal.rollEditor))
							.measure(key: RollEditorSizeKey.self, to: $rollEditorSize)
							.padding(.horizontal)

						if store.isScoreSheetVisible {
							scoreSheet
								.padding(.top)
								.padding(.horizontal)
								.measure(key: FrameContentSizeKey.self, to: $frameContentSize)
						}
					}
				}
				.frame(idealWidth: store.backdropSize.width, maxHeight: store.backdropSize.height)

				Spacer()
			}
			.measure(key: WindowContentSizeKey.self, to: $windowContentSize)
			.background(alignment: .top) {
				VStack(spacing: 0) {
					Asset.Media.Lane.galaxy.swiftUIImage
						.resizable()
						.scaledToFill()
					Asset.Media.Lane.wood.swiftUIImage
						.resizable()
						.scaledToFill()
				}
				.frame(width: store.backdropSize.width, height: backdropImageHeight)
				.faded()
				.clipped()
				.padding(.top, headerContentSize.height)
			}
			.background(Color.black)
			.toolbar(.hidden, for: .tabBar, .navigationBar)
			.sheet(
				item: $store.scope(state: \.destination?.gameDetails, action: \.internal.destination.gameDetails),
				onDismiss: { send(.didDismissGameDetails) },
				content: { (store: StoreOf<GameDetails>) in
					WithPerceptionTracking {
						gameDetails(gameDetailsStore: store)
					}
				}
			)
			.onChange(of: store.willAdjustLaneLayoutAt) { _ in
				send(.didAdjustBackdropSize(measuredBackdropSize), animation: .easeInOut)
			}
			.onChange(of: store.shouldRequestAppStoreReview) { shouldRequestAppStoreReview in
				if shouldRequestAppStoreReview {
					requestReview()
					send(.didRequestReview)
				}
			}
			.onAppear { send(.onAppear) }
			.onFirstAppear {
				send(.didFirstAppear)
				Task.detached {
					try await clock.sleep(for: .milliseconds(150))
					Task.detached { @MainActor in
						send(.didAdjustBackdropSize(measuredBackdropSize))
					}
				}
			}
			// TODO: enable errors
//			.errors(store: store.scope(state: \.errors, action: \.internal.errors))
			.alert(
				$store.scope(
					state: \.destination?.duplicateLanesAlert,
					action: \.internal.destination.duplicateLanesAlert
				)
			)
			.alert(
				$store.scope(
					state: \.destination?.lockedAlert,
					action: \.internal.destination.lockedAlert
				)
			)
			.ballPicker(
				$store.scope(state: \.destination?.sheets?.ballPicker, action: \.internal.destination.sheets.ballPicker),
				onDismiss: { send(.didDismissOpenSheet) }
			)
			.settings(
				$store.scope(state: \.destination?.sheets?.settings, action: \.internal.destination.sheets.settings),
				onDismiss: { send(.didDismissOpenSheet) }
			)
			.sharing(
				$store.scope(state: \.destination?.sheets?.sharing, action: \.internal.destination.sheets.sharing),
				onDismiss: { send(.didDismissOpenSheet) }
			)
		}
	}

	private func gameDetails(gameDetailsStore: StoreOf<GameDetails>) -> some View {
		GameDetailsView(store: gameDetailsStore)
			.padding(.top, -store.gameDetailsHeaderSize.height)
			.frame(minHeight: 50)
			.edgesIgnoringSafeArea(.bottom)
			.presentationDetents(
				[
					.height(store.gameDetailsMinimumContentSize.height + 40),
					.medium,
					.large,
				],
				selection: $store.sheetDetent
			)
			.presentationBackgroundInteraction(.enabled(upThrough: .medium))
			.interactiveDismissDisabled(true)
			.measure(key: SheetContentSizeKey.self, to: $sheetContentSize)
	}

	@ViewBuilder private var scoreSheet: some View {
		if let game = store.score {
			ScoreSheet(game: game, selection: $store.currentFrame)
		}
	}

	private var measuredBackdropSize: CGSize {
		let sheetContentSize = store.sheetDetent == .large ? .zero : self.sheetContentSize
		return .init(
			width: windowContentSize.width,
			height: windowContentSize.height
				- sheetContentSize.height
				- headerContentSize.height
				- safeAreaInsets.bottom
				- CGFloat.largeSpacing
		)
	}

	private var backdropImageHeight: CGFloat {
		max(
			store.backdropSize.height
				- (store.isScoreSheetVisible ? frameContentSize.height : 0)
				- headerContentSize.height
				+ rollEditorSize.height,
			0
		)
	}
}

@MainActor extension View {
	fileprivate func ballPicker(
		_ store: Binding<StoreOf<ResourcePicker<Gear.Summary, AlwaysEqual<Void>>>?>,
		onDismiss: @escaping () -> Void
	) -> some View {
		sheet(item: store, onDismiss: onDismiss) { store in
			NavigationStack {
				ResourcePickerView(store: store) {
					Gear.ViewWithAvatar($0)
				}
			}
		}
	}

	fileprivate func settings(_ store: Binding<StoreOf<GamesSettings>?>, onDismiss: @escaping () -> Void) -> some View {
		sheet(item: store, onDismiss: onDismiss) { store in
			NavigationStack {
				GamesSettingsView(store: store)
			}
		}
	}

	fileprivate func sharing(_ store: Binding<StoreOf<Sharing>?>, onDismiss: @escaping () -> Void) -> some View {
		sheet(item: store, onDismiss: onDismiss) { store in
			NavigationStack {
				SharingView(store: store)
			}
		}
	}
}

private struct SheetContentSizeKey: PreferenceKey, CGSizePreferenceKey {}
private struct WindowContentSizeKey: PreferenceKey, CGSizePreferenceKey {}
private struct HeaderContentSizeKey: PreferenceKey, CGSizePreferenceKey {}
private struct FrameContentSizeKey: PreferenceKey, CGSizePreferenceKey {}
private struct RollEditorSizeKey: PreferenceKey, CGSizePreferenceKey {}
