import AssetsLibrary
import AvatarServiceInterface
import ComposableArchitecture
import EquatablePackageLibrary
import ErrorsFeature
import ExtensionsPackageLibrary
import FeatureActionLibrary
import ModelsLibrary
import ResourcePickerLibrary
import ScoreSheetLibrary
import SharingFeature
import StoreKit
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsPackageLibrary

@ViewAction(for: GamesEditor.self)
public struct GamesEditorView: View {
	@Bindable public var store: StoreOf<GamesEditor>

	@Environment(\.continuousClock) private var clock
	@Environment(\.safeAreaInsetsProvider) private var safeAreaInsetsProvider
	@Environment(\.requestReview) private var requestReview

	public init(store: StoreOf<GamesEditor>) {
		self.store = store
	}

	public var body: some View {
		VStack {
			GamesHeaderView(store: store.scope(state: \.gamesHeader, action: \.internal.gamesHeader))
				.measure(key: HeaderContentSizeKey.self, to: $store.headerContentSize)

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
					VStack {
						Spacer()

						FrameEditorView(store: store.scope(state: \.frameEditor, action: \.internal.frameEditor))
							.padding(.top)

						Spacer()
					}
					.overlay(alignment: .topLeading) {
						if store.isFrameDragHintVisible {
							FrameDragHint {
								send(.didDismissFrameDragHint, animation: .default)
							}
						}
					}

					RollEditorView(store: store.scope(state: \.rollEditor, action: \.internal.rollEditor))
						.measure(key: RollEditorSizeKey.self, to: $store.rollEditorSize)
						.padding(.horizontal)

					if store.isScoreSheetVisible {
						scoreSheet
							.padding(.top)
							.padding(.horizontal)
							.measure(key: FrameContentSizeKey.self, to: $store.frameContentSize)
					}
				}
			}
			.frame(idealWidth: store.measuredBackdropSize.width, maxHeight: store.measuredBackdropSize.height)

			Spacer()
		}
		.measure(key: WindowContentSizeKey.self, to: $store.windowContentSize)
		.onChange(of: safeAreaInsetsProvider.get()) { send(.didChangeSafeAreaInsets(safeAreaInsetsProvider.get())) }
		.background(alignment: .top) {
			VStack(spacing: 0) {
				Asset.Media.Lane.galaxy.swiftUIImage
					.resizable()
					.scaledToFill()
				Asset.Media.Lane.wood.swiftUIImage
					.resizable()
					.scaledToFill()
			}
			.frame(width: store.measuredBackdropSize.width, height: store.backdropImageHeight)
			.faded()
			.clipped()
			.padding(.top, store.headerContentSize.height)
		}
		.background(Color.black)
		.toolbar(.hidden, for: .tabBar, .navigationBar)
		.sheet(
			item: $store.scope(state: \.destination?.gameDetails, action: \.internal.destination.gameDetails),
			onDismiss: { send(.didDismissGameDetails) },
			content: { (store: StoreOf<GameDetails>) in
				gameDetails(gameDetailsStore: store)
			}
		)
		.onChange(of: store.shouldRequestAppStoreReview) {
			if store.shouldRequestAppStoreReview {
				requestReview()
				send(.didRequestReview)
			}
		}
		.onAppear { send(.onAppear) }
		.onFirstAppear { send(.didFirstAppear) }
		.errors(store: store.scope(state: \.errors, action: \.internal.errors))
		.toast($store.scope(state: \.toast, action: \.internal.toast))
		.alert(
			$store.scope(
				state: \.destination?.duplicateLanesAlert,
				action: \.internal.destination.duplicateLanesAlert
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
			.measure(key: SheetContentSizeKey.self, to: $store.sheetContentSize)
	}

	@ViewBuilder private var scoreSheet: some View {
		if let game = store.score {
			ScoreSheetScrollView(game: game, configuration: .plain, selection: $store.currentFrame)
		}
	}
}

extension View {
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
