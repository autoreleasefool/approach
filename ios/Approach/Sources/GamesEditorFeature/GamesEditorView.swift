import AssetsLibrary
import AvatarServiceInterface
import ComposableArchitecture
import EquatableLibrary
import ErrorsFeature
import FeatureActionLibrary
import ModelsLibrary
import ResourcePickerLibrary
import ScoreSheetLibrary
import SharingFeature
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary

// swiftlint:disable:next type_body_length
public struct GamesEditorView: View {
	let store: StoreOf<GamesEditor>
	typealias GamesEditorViewStore = ViewStore<ViewState, GamesEditor.Action.ViewAction>

	@Environment(\.continuousClock) private var clock
	@Environment(\.safeAreaInsets) private var safeAreaInsets
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
				GamesHeaderView(store: store.scope(state: \.gamesHeader, action: /GamesEditor.Action.InternalAction.gamesHeader))
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
				store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
				state: /GamesEditor.Destination.State.gameDetails,
				action: GamesEditor.Destination.Action.gameDetails,
				onDismiss: { viewStore.send(.didDismissGameDetails) }
			) { (store: StoreOf<GameDetails>) in
				gameDetails(viewStore: viewStore, gameDetailsStore: store)
			}
			.onChange(of: viewStore.willAdjustLaneLayoutAt) { _ in
				viewStore.send(.didAdjustBackdropSize(getMeasuredBackdropSize(viewStore)), animation: .easeInOut)
			}
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
		.errors(store: store.scope(state: \.errors, action: { .internal(.errors($0)) }))
		.alert(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /GamesEditor.Destination.State.duplicateLanesAlert,
			action: GamesEditor.Destination.Action.duplicateLanesAlert
		)
		.sheet(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /GamesEditor.Destination.State.sheets,
			action: GamesEditor.Destination.Action.sheets,
			onDismiss: { store.send(.view(.didDismissOpenSheet)) }
		) {
			SwitchStore($0) { state in
				switch state {
				case .ballPicker:
					CaseLet(
						/GamesEditor.SheetsDestination.State.ballPicker,
						action: GamesEditor.SheetsDestination.Action.ballPicker
					) {
						ballPicker(store: $0)
					}
				case .settings:
					CaseLet(
						/GamesEditor.SheetsDestination.State.settings,
						action: GamesEditor.SheetsDestination.Action.settings
					) {
						gamesSettings(store: $0)
					}
				case .sharing:
					CaseLet(
						/GamesEditor.SheetsDestination.State.sharing,
						action: GamesEditor.SheetsDestination.Action.sharing
					) {
						sharing(store: $0)
					}
				}
			}
		}
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
			.toast(store: store.scope(state: \.toast, action: { .internal(.toast($0)) }))
			.measure(key: SheetContentSizeKey.self, to: $sheetContentSize)
	}

	private func ballPicker(store: StoreOf<ResourcePicker<Gear.Summary, AlwaysEqual<Void>>>) -> some View {
		NavigationStack {
			ResourcePickerView(store: store) {
				Gear.ViewWithAvatar($0)
			}
		}
	}

	private func gamesSettings(store: StoreOf<GamesSettings>) -> some View {
		NavigationStack {
			GamesSettingsView(store: store)
		}
	}

	private func sharing(store: StoreOf<Sharing>) -> some View {
		NavigationStack {
			SharingView(store: store)
		}
	}

	private var frameEditor: some View {
		IfLetStore(store.scope(state: \.frameEditor, action: /GamesEditor.Action.InternalAction.frameEditor)) {
			FrameEditorView(store: $0)
		}
	}

	private var rollEditor: some View {
		IfLetStore(store.scope(state: \.rollEditor, action: /GamesEditor.Action.InternalAction.rollEditor)) {
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

private struct SheetContentSizeKey: PreferenceKey, CGSizePreferenceKey {}
private struct WindowContentSizeKey: PreferenceKey, CGSizePreferenceKey {}
private struct HeaderContentSizeKey: PreferenceKey, CGSizePreferenceKey {}
private struct FrameContentSizeKey: PreferenceKey, CGSizePreferenceKey {}
private struct RollEditorSizeKey: PreferenceKey, CGSizePreferenceKey {}
