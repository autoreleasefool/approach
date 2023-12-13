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

public struct GamesEditorView: View {
	let store: StoreOf<GamesEditor>
	typealias GamesEditorViewStore = ViewStore<ViewState, GamesEditor.Action.ViewAction>

	@Environment(\.continuousClock) private var clock
	@Environment(\.safeAreaInsets) private var safeAreaInsets
	@Environment(\.requestReview) private var requestReview
	@State private var headerContentSize: CGSize = .zero
	@State private var rollEditorSize: CGSize = .zero
	@State private var frameContentSize: CGSize = .zero
	@State private var sheetContentSize: CGSize = .zero
	@State private var windowContentSize: CGSize = .zero

	struct ViewState: Equatable {
		@BindingViewState var sheetDetent: PresentationDetent
		let gameDetailsHeaderSize: CGSize
		let gameDetailsMinimumContentSize: CGSize
		let willAdjustLaneLayoutAt: Date
		let backdropSize: CGSize

		let shouldRequestAppStoreReview: Bool

		let isScoreSheetVisible: Bool
		let score: ScoredGame?
		@BindingViewState var currentFrame: ScoreSheet.Selection

		let manualScore: Int?

		let bowlerName: String?
		let leagueName: String?
	}

	public init(store: StoreOf<GamesEditor>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: { .view($0) }, content: { viewStore in
			VStack {
				GamesHeaderView(store: store.scope(state: \.gamesHeader, action: \.internal.gamesHeader))
					.measure(key: HeaderContentSizeKey.self, to: $headerContentSize)

				VStack {
					if let manualScore = viewStore.manualScore {
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

						frameEditor
							.padding(.top)

						Spacer()

						rollEditor
							.measure(key: RollEditorSizeKey.self, to: $rollEditorSize)
							.padding(.horizontal)

						if viewStore.isScoreSheetVisible {
							scoreSheet(viewStore)
								.padding(.top)
								.padding(.horizontal)
								.measure(key: FrameContentSizeKey.self, to: $frameContentSize)
						}
					}
				}
				.frame(idealWidth: viewStore.backdropSize.width, maxHeight: viewStore.backdropSize.height)

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
				.frame(width: viewStore.backdropSize.width, height: getBackdropImageHeight(viewStore))
				.faded()
				.clipped()
				.padding(.top, headerContentSize.height)
			}
			.background(Color.black)
			.toolbar(.hidden, for: .tabBar, .navigationBar)
			.sheet(
				store: store.scope(state: \.$destination, action: \.internal.destination),
				state: /GamesEditor.Destination.State.gameDetails,
				action: GamesEditor.Destination.Action.gameDetails,
				onDismiss: { viewStore.send(.didDismissGameDetails) },
				content: { (store: StoreOf<GameDetails>) in
					gameDetails(viewStore: viewStore, gameDetailsStore: store)
				}
			)
			.onChange(of: viewStore.willAdjustLaneLayoutAt) { _ in
				viewStore.send(.didAdjustBackdropSize(getMeasuredBackdropSize(viewStore)), animation: .easeInOut)
			}
			.onChange(of: viewStore.shouldRequestAppStoreReview) { shouldRequestAppStoreReview in
				if shouldRequestAppStoreReview {
					requestReview()
					viewStore.send(.didRequestReview)
				}
			}
			.onAppear { viewStore.send(.onAppear) }
			.onFirstAppear {
				viewStore.send(.didFirstAppear)
				Task.detached {
					try await clock.sleep(for: .milliseconds(150))
					Task.detached { @MainActor in
						viewStore.send(.didAdjustBackdropSize(getMeasuredBackdropSize(viewStore)))
					}
				}
			}
		})
		.errors(store: store.scope(state: \.errors, action: \.internal.errors))
		.alert(
			store: store.scope(
				state: \.$destination.duplicateLanesAlert,
				action: \.internal.destination.duplicateLanesAlert
			)
		)
		.ballPicker(
			store.scope(state: \.$destination.sheets.ballPicker, action: \.internal.destination.sheets.ballPicker),
			onDismiss: { store.send(.view(.didDismissOpenSheet)) }
		)
		.settings(
			store.scope(state: \.$destination.sheets.settings, action: \.internal.destination.sheets.settings),
			onDismiss: { store.send(.view(.didDismissOpenSheet)) }
		)
		.sharing(
			store.scope(state: \.$destination.sheets.sharing, action: \.internal.destination.sheets.sharing),
			onDismiss: { store.send(.view(.didDismissOpenSheet)) }
		)
	}

	private func gameDetails(
		viewStore: GamesEditorViewStore,
		gameDetailsStore: StoreOf<GameDetails>
	) -> some View {
		GameDetailsView(store: gameDetailsStore)
			.padding(.top, -viewStore.gameDetailsHeaderSize.height)
			.frame(minHeight: 50)
			.edgesIgnoringSafeArea(.bottom)
			.presentationDetents(
				[
					.height(viewStore.gameDetailsMinimumContentSize.height + 40),
					.medium,
					.large,
				],
				selection: viewStore.$sheetDetent
			)
			.presentationBackgroundInteraction(.enabled(upThrough: .medium))
			.interactiveDismissDisabled(true)
			.toast(store: store.scope(state: \.toast, action: \.internal.toast))
			.measure(key: SheetContentSizeKey.self, to: $sheetContentSize)
	}

	private var frameEditor: some View {
		IfLetStore(store.scope(state: \.frameEditor, action: \.internal.frameEditor)) {
			FrameEditorView(store: $0)
		}
	}

	private var rollEditor: some View {
		IfLetStore(store.scope(state: \.rollEditor, action: \.internal.rollEditor)) {
			RollEditorView(store: $0)
		}
	}

	@MainActor @ViewBuilder private func scoreSheet(
		_ viewStore: ViewStore<ViewState, GamesEditor.Action.ViewAction>
	) -> some View {
		if let game = viewStore.score {
			ScoreSheet(game: game, selection: viewStore.$currentFrame)
		}
	}

	private func getMeasuredBackdropSize(_ viewStore: GamesEditorViewStore) -> CGSize {
		let sheetContentSize = viewStore.sheetDetent == .large ? .zero : self.sheetContentSize
		return .init(
			width: windowContentSize.width,
			height: windowContentSize.height
				- sheetContentSize.height
				- headerContentSize.height
				- safeAreaInsets.bottom
				- CGFloat.largeSpacing
		)
	}

	private func getBackdropImageHeight(_ viewStore: GamesEditorViewStore) -> CGFloat {
		max(
			viewStore.backdropSize.height
				- (viewStore.isScoreSheetVisible ? frameContentSize.height : 0)
				- headerContentSize.height
				+ rollEditorSize.height,
			0
		)
	}
}

extension GamesEditorView.ViewState {
	init(store: BindingViewStore<GamesEditor.State>) {
		self._sheetDetent = store.$sheetDetent
		self._currentFrame = store.$currentFrame
		self.gameDetailsHeaderSize = store.gameDetailsHeaderSize
		self.gameDetailsMinimumContentSize = store.gameDetailsMinimumContentSize
		self.willAdjustLaneLayoutAt = store.willAdjustLaneLayoutAt
		self.backdropSize = store.backdropSize
		self.isScoreSheetVisible = store.isScoreSheetVisible
		self.score = store.score
		self.bowlerName = store.game?.bowler.name
		self.leagueName = store.game?.league.name
		self.shouldRequestAppStoreReview = store.shouldRequestAppStoreReview
		if let game = store.game {
			switch game.scoringMethod {
			case .byFrame:
				self.manualScore = nil
			case .manual:
				self.manualScore = game.score
			}
		} else {
			self.manualScore = nil
		}
	}
}

@MainActor extension View {
	fileprivate func ballPicker(
		_ store: PresentationStoreOf<ResourcePicker<Gear.Summary, AlwaysEqual<Void>>>,
		onDismiss: @escaping () -> Void
	) -> some View {
		sheet(store: store, onDismiss: onDismiss) { store in
			NavigationStack {
				ResourcePickerView(store: store) {
					Gear.ViewWithAvatar($0)
				}
			}
		}
	}

	fileprivate func settings(
		_ store: PresentationStoreOf<GamesSettings>,
		onDismiss: @escaping () -> Void
	) -> some View {
		sheet(store: store, onDismiss: onDismiss) { store in
			NavigationStack {
				GamesSettingsView(store: store)
			}
		}
	}

	fileprivate func sharing(
		_ store: PresentationStoreOf<Sharing>,
		onDismiss: @escaping () -> Void
	) -> some View {
		sheet(store: store, onDismiss: onDismiss) { store in
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
