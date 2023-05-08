import AssetsLibrary
import ComposableArchitecture
import FeatureActionLibrary
import ModelsLibrary
import ScoreSheetFeature
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary

public struct GamesEditorView: View {
	let store: StoreOf<GamesEditor>

	@Environment(\.continuousClock) private var clock
	@Environment(\.safeAreaInsets) private var safeAreaInsets
	@State private var headerContentSize: CGSize = .zero
	@State private var frameContentSize: CGSize = .zero
	@State private var sheetContentSize: CGSize = .zero
	@State private var windowContentSize: CGSize = .zero
	@State private var minimumSheetContentSize: CGSize = .zero

	struct ViewState: Equatable {
		let sheetDetent: PresentationDetent
		let willAdjustLaneLayoutAt: Date
		let backdropSize: CGSize

		let isGamePickerPresented: Bool
		let isGameDetailsPresented: Bool
		let isBowlingBallPickerPresented: Bool
		let isGamesSettingsPresented: Bool

		let isGameStatsVisible: Bool

		let bowlerName: String?
		let leagueName: String?

		init(state: GamesEditor.State) {
			self.sheetDetent = state.sheetDetent
			self.willAdjustLaneLayoutAt = state.willAdjustLaneLayoutAt
			self.backdropSize = state.backdropSize
			self.isGameDetailsPresented = state.sheet == .presenting(.gameDetails)
			self.isGamePickerPresented = state.sheet == .presenting(.gamePicker)
			self.isBowlingBallPickerPresented = state.sheet == .presenting(.bowlingBallPicker)
			self.isGamesSettingsPresented = state.sheet == .presenting(.settings)
			self.isGameStatsVisible = state.isGameStatsVisible
			self.bowlerName = state.currentGame?.bowler.name
			self.leagueName = state.currentGame?.league.name
		}
	}

	enum ViewAction {
		case didAppear
		case didTapClose
		case didTapSettings
		case didChangeDetent(PresentationDetent)
		case didAdjustBackdropSize(CGSize)

		case setGamePicker(isPresented: Bool)
		case setGameDetails(isPresented: Bool)
		case setBowlingBallPicker(isPresented: Bool)
		case setGamesSettings(isPresented: Bool)

		case didDismissGameDetails
		case didDismissGamePicker
		case didDismissBowlingBallPicker
		case didDismissGamesSettings
	}

	public init(store: StoreOf<GamesEditor>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: GamesEditor.Action.init) { viewStore in
			VStack {
				HStack {
					headerButton(systemName: "chevron.backward") { viewStore.send(.didTapClose) }
					Spacer()
					GameIndicatorView(
						store: store.scope(state: \.gameIndicator, action: /GamesEditor.Action.InternalAction.gameIndicator)
					)
					Spacer()
					headerButton(systemName: "gear") { viewStore.send(.didTapSettings) }
				}
				.measure(key: HeaderContentSizeKey.self, to: $headerContentSize)
				VStack {
					Spacer()
					IfLetStore(
						store.scope(state: \.frameEditor, action: /GamesEditor.Action.InternalAction.frameEditor)
					) { scopedStore in
						FrameEditorView(store: scopedStore)
							.padding(.top)
					}
					Spacer()
					IfLetStore(
						store.scope(state: \.rollEditor, action: /GamesEditor.Action.InternalAction.rollEditor)
					) { scopedStore in
						RollEditorView(store: scopedStore)
							.padding(.horizontal)
					}

					if viewStore.isGameStatsVisible {
						IfLetStore(
							store.scope(state: \.scoreSheet, action: /GamesEditor.Action.InternalAction.scoreSheet)
						) { scopedStore in
							ScoreSheetView(store: scopedStore)
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
				Image(uiImage: .laneBackdrop)
					.resizable(resizingMode: .stretch)
					.fixedSize(horizontal: true, vertical: false)
					.frame(width: viewStore.backdropSize.width, height: getBackdropHeight(viewStore))
					.padding(.top, headerContentSize.height)
			}
			.background(Color.black)
			.toolbar(.hidden, for: .tabBar, .navigationBar)
			.sheet(isPresented: viewStore.binding(
				get: \.isGamePickerPresented,
				send: ViewAction.setGamePicker(isPresented:)
			), onDismiss: {
				viewStore.send(.didDismissGamePicker)
			}, content: {
				NavigationView {
					GamePickerView(store: store.scope(state: \.gamePicker, action: /GamesEditor.Action.InternalAction.gamePicker))
				}
				.presentationDetents([.medium])
			})
			.sheet(isPresented: viewStore.binding(
				get: \.isBowlingBallPickerPresented,
				send: ViewAction.setBowlingBallPicker(isPresented:)
			), onDismiss: {
				viewStore.send(.didDismissBowlingBallPicker)
			}, content: {
				NavigationView {
					BowlingBallPickerView(
						store: store.scope(state: \.bowlingBallPicker, action: /GamesEditor.Action.InternalAction.bowlingBallPicker)
					)
				}
				.presentationDetents([.medium])
			})
			.sheet(isPresented: viewStore.binding(
				get: \.isGamesSettingsPresented,
				send: ViewAction.setGamesSettings(isPresented:)
			), onDismiss: {
				viewStore.send(.didDismissGamesSettings)
			}, content: {
				IfLetStore(
					store.scope(state: \.gamesSettings, action: /GamesEditor.Action.InternalAction.gamesSettings)
				) { scopedStore in
					NavigationView {
						GamesSettingsView(store: scopedStore)
					}
				}
				.presentationDetents([.large])
			})
			.sheet(isPresented: viewStore.binding(
				get: \.isGameDetailsPresented,
				send: ViewAction.setGameDetails(isPresented:)
			), onDismiss: {
				viewStore.send(.didDismissGameDetails)
			}, content: {
				Form {
					if let bowlerName = viewStore.bowlerName, let leagueName = viewStore.leagueName {
						GameSummaryHeader(bowlerName: bowlerName, leagueName: leagueName)
							.measure(key: MinimumSheetContentSizeKey.self, to: $minimumSheetContentSize)
					}

					IfLetStore(
						store.scope(state: \.gameDetails, action: /GamesEditor.Action.InternalAction.gameDetails)
					) { scopedStore in
						GameDetailsView(store: scopedStore)
					}
				}
				.frame(minHeight: 30)
				.edgesIgnoringSafeArea(.bottom)
				.presentationDetents(
					[
						.height(minimumSheetContentSize.height + 48),
						.medium,
						.large
					],
					selection: viewStore.binding(get: \.sheetDetent, send: ViewAction.didChangeDetent)
				)
				.presentationBackgroundInteraction(.enabled(upThrough: .medium))
				.interactiveDismissDisabled(true)
				.measure(key: SheetContentSizeKey.self, to: $sheetContentSize)
			})
			.onChange(of: viewStore.willAdjustLaneLayoutAt) { _ in
				viewStore.send(.didAdjustBackdropSize(getMeasuredBackdropSize(viewStore)), animation: .easeInOut)
			}
			.onAppear {
				viewStore.send(.didAppear)
				Task.detached {
					try await clock.sleep(for: .milliseconds(100))
					Task.detached { @MainActor in
						viewStore.send(.didAdjustBackdropSize(getMeasuredBackdropSize(viewStore)))
					}
				}
			}
		}
	}

	private func headerButton(systemName: String, action: @escaping () -> Void) -> some View {
		Button(action: action) {
			Image(systemName: systemName)
				.resizable()
				.scaledToFit()
				.frame(width: .smallIcon, height: .smallIcon)
				.foregroundColor(.white)
				.padding()
		}
	}

	private func getMeasuredBackdropSize(_ viewStore: ViewStore<ViewState, ViewAction>) -> CGSize {
		let sheetContentSize = viewStore.sheetDetent == .large ? .zero : self.sheetContentSize
		return .init(
			width: windowContentSize.width,
			height: windowContentSize.height - sheetContentSize.height - headerContentSize.height - safeAreaInsets.bottom - CGFloat.largeSpacing
		)
	}

	private func getBackdropHeight(_ viewStore: ViewStore<ViewState, ViewAction>) -> CGFloat {
		max(viewStore.backdropSize.height - (viewStore.isGameStatsVisible ? frameContentSize.height : 0), 0)
	}
}

extension GamesEditor.Action {
	init(action: GamesEditorView.ViewAction) {
		switch action {
		case .didAppear:
			self = .view(.didAppear)
		case .didTapClose:
			self = .view(.didTapClose)
		case .didTapSettings:
			self = .view(.didTapSettings)
		case let .setGameDetails(isPresented):
			self = .view(.setGameDetails(isPresented: isPresented))
		case let .setGamePicker(isPresented):
			self = .view(.setGamePicker(isPresented: isPresented))
		case let .setBowlingBallPicker(isPresented):
			self = .view(.setBowlingBallPicker(isPresented: isPresented))
		case let .setGamesSettings(isPresented):
			self = .view(.setGamesSettings(isPresented: isPresented))
		case .didDismissGamePicker:
			self = .view(.didDismissOpenSheet)
		case .didDismissGameDetails:
			self = .view(.didDismissOpenSheet)
		case .didDismissBowlingBallPicker:
			self = .view(.didDismissOpenSheet)
		case .didDismissGamesSettings:
			self = .view(.didDismissOpenSheet)
		case let .didChangeDetent(newDetent):
			self = .view(.didChangeDetent(newDetent))
		case let .didAdjustBackdropSize(newSize):
			self = .view(.didAdjustBackdropSize(newSize))
		}
	}
}

private struct MinimumSheetContentSizeKey: PreferenceKey {
	static var defaultValue: CGSize = .zero
	static func reduce(value: inout CGSize, nextValue: () -> CGSize) {
		value = nextValue()
	}
}

private struct SheetContentSizeKey: PreferenceKey {
	static var defaultValue: CGSize = .zero
	static func reduce(value: inout CGSize, nextValue: () -> CGSize) {
		value = nextValue()
	}
}

private struct WindowContentSizeKey: PreferenceKey {
	static var defaultValue: CGSize = .zero
	static func reduce(value: inout CGSize, nextValue: () -> CGSize) {
		value = nextValue()
	}
}

private struct HeaderContentSizeKey: PreferenceKey {
	static var defaultValue: CGSize = .zero
	static func reduce(value: inout CGSize, nextValue: () -> CGSize) {
		value = nextValue()
	}
}

private struct FrameContentSizeKey: PreferenceKey {
	static var defaultValue: CGSize = .zero
	static func reduce(value: inout CGSize, nextValue: () -> CGSize) {
		value = nextValue()
	}
}
