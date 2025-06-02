import AssetsLibrary
import ComposableArchitecture
import FeatureActionLibrary
import ModelsLibrary
import QuickLaunchRepositoryInterface
import SeriesRepositoryInterface
import StringsLibrary
import SwiftUI
import TipsServiceInterface
import ViewsLibrary

@Reducer
public struct QuickLaunch: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		public var source: Source = .notLoaded
		public var isShowingTip: Bool

		init() {
			@Dependency(TipsService.self) var tips
			self.isShowingTip = tips.shouldShow(tipFor: .quickLaunchTip)
		}
	}

	public enum Action: FeatureAction, ViewAction {
		@CasePathable
		public enum View {
			case onAppear
			case didTapStartButton
		}

		@CasePathable
		public enum Delegate {
			case createSeries(Series.Create, League.SeriesHost)
		}

		@CasePathable
		public enum Internal {
			case didLoadSource(Result<QuickLaunchSource?, Error>)
		}

		case view(View)
		case `internal`(Internal)
		case delegate(Delegate)
	}

	public enum Source: Equatable {
		case notLoaded
		case loaded(QuickLaunchSource?)

		var value: QuickLaunchSource? {
			switch self {
			case let .loaded(t): t
			case .notLoaded: nil
			}
		}
	}

	@Dependency(\.calendar) var calendar
	@Dependency(\.date) var date
	@Dependency(QuickLaunchRepository.self) var quickLaunch
	@Dependency(TipsService.self) var tips
	@Dependency(\.uuid) var uuid

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					return .run { send in
						await send(.internal(.didLoadSource(Result {
							try await quickLaunch.defaultSource()
						})))
					}

				case .didTapStartButton:
					guard let league = state.source.value?.league else { return .none }
					state.isShowingTip = false

					let series = Series.Create.default(
						withId: uuid(),
						onDate: calendar.startOfDay(for: date()),
						inLeague: league
					)

					return .run { send in
						await send(.delegate(.createSeries(series, league)))
						await tips.hide(tipFor: .quickLaunchTip)
					}
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .didLoadSource(.success(source)):
					state.source = .loaded(source)
					return .none

				case .didLoadSource(.failure):
					// Intentionally drop errors
					state.source = .loaded(nil)
					return .none
				}

			case .delegate:
				return .none
			}
		}
	}
}

// MARK: - View

@ViewAction(for: QuickLaunch.self)
public struct QuickLaunchView: View {
	public let store: StoreOf<QuickLaunch>

	init(store: StoreOf<QuickLaunch>) {
		self.store = store
	}

	public var body: some View {
		switch store.source {
		case .notLoaded:
			ProgressView()
				.onAppear { send(.onAppear) }
		case let .loaded(source):
			if let source {
				startButtonSection(source: source)

				if store.isShowingTip {
					tipSection
				}
			}
		}
	}

	private func startButtonSection(source: QuickLaunchSource) -> some View {
		Section {
			Button { send(.didTapStartButton, animation: .default) } label: {
				HStack(spacing: 0) {
					bowlerDetails(name: source.bowler.name, league: source.league.name)

					Spacer(minLength: .standardSpacing)

					explanation
				}
				.contentShape(.rect)
			}
			.modifier(PrimaryButton())
		}
		.listRowInsets(EdgeInsets())
		.listSectionSpacing(.compact)
	}

	private func bowlerDetails(name: String, league: String) -> some View {
		HStack(spacing: .standardSpacing) {
			Image(systemName: "figure.bowling")
				.resizable()
				.scaledToFit()
				.frame(width: .smallIcon, height: .smallIcon)
				.foregroundStyle(Asset.Colors.Text.onAction)

			VStack(alignment: .leading) {
				Text(name)
					.font(.headline)
					.frame(maxWidth: .infinity, alignment: .leading)
				Text(league)
					.font(.subheadline)
					.frame(maxWidth: .infinity, alignment: .leading)
			}
			.frame(maxWidth: .infinity)
		}
	}

	private var explanation: some View {
		HStack(spacing: .standardSpacing) {
			VStack(alignment: .trailing) {
				Text(Strings.QuickLaunch.BowlersList.title)
					.font(.subheadline)

				Text(Strings.QuickLaunch.BowlersList.subtitle)
					.font(.caption)
			}

			Image(systemName: "chevron.right")
				.font(.caption)
		}
	}

	private var tipSection: some View {
		Section {
			BasicTipView(
				tip: .quickLaunchTip,
				isDismissable: false,
				onDismiss: {}
			)
		}
		.listSectionSpacing(.compact)
	}
}

// MARK: - Tip

extension Tip {
	static let quickLaunchTip = Tip(
		id: "Bowlers.List.QuickLaunch",
		title: Strings.QuickLaunch.BowlersList.Tip.title,
		message: Strings.QuickLaunch.BowlersList.Tip.message
	)
}
