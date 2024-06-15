import AnalyticsServiceInterface
import AssetsLibrary
import ComposableArchitecture
import ErrorsFeature
import ExtensionsPackageLibrary
import FeatureActionLibrary
import ModelsLibrary
import SeriesRepositoryInterface
import StringsLibrary
import SwiftUI
import ViewsLibrary

@Reducer
public struct SeriesSharing: Reducer {
	@ObservableState
	public struct State: Equatable {
		public let seriesId: Series.ID
		public var series: Series.Shareable?

		public var scoreUpperBound: Int = Game.MAXIMUM_SCORE
		public var scoreLowerBound: Int = 0

		public var isShowingSeriesDate: Bool = true
		public var isShowingSeriesSummary: Bool = true
		public var isShowingBowlerName: Bool = false
		public var isShowingLeagueName: Bool = false
		public var isLabellingHighestScore: Bool = false
		public var isLabellingLowestScore: Bool = false

		public var scoreLowerBoundRange: [Int] = []
		public var scoreUpperBoundRange: [Int] = []

		public var errors: Errors<ErrorID>.State = .init()

		public var displayScale: CGFloat = .zero
		public var preferredAppearance: Appearance = .dark

		var configuration: ShareableSeriesImage.Configuration? {
			guard let series else { return nil }
			return .init(
				date: isShowingSeriesDate ? series.date : nil,
				total: series.total,
				showDetails: isShowingSeriesSummary,
				scores: series.scores,
				bowlerName: isShowingBowlerName ? series.bowlerName : nil,
				leagueName: isShowingLeagueName ? series.leagueName : nil,
				labelHighestScore: isLabellingHighestScore,
				labelLowestScore: isLabellingLowestScore,
				scoreDomain: scoreLowerBound...scoreUpperBound,
				displayScale: displayScale,
				colorScheme: preferredAppearance.colorScheme
			)
		}

		public init(seriesId: Series.ID) {
			self.seriesId = seriesId
		}
	}

	public enum Action: FeatureAction, ViewAction, BindableAction {
		@CasePathable public enum View {
			case task
			case didUpdateDisplayScale(CGFloat)
			case didUpdateColorScheme(ColorScheme)
		}
		@CasePathable public enum Delegate {
			case imageRendered(UIImage)
		}
		@CasePathable public enum Internal {
			case loadSeriesResponse(Result<Series.Shareable, Error>)
			case errors(Errors<ErrorID>.Action)
		}

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
		case binding(BindingAction<State>)
	}

	public enum ErrorID: Hashable {
		case seriesNotFound
	}

	public enum CancelID: Hashable {
		case imageRenderer
	}

	public init() {}

	@Dependency(SeriesRepository.self) var series

	public var body: some ReducerOf<Self> {
		Scope(state: \.errors, action: \.internal.errors) {
			Errors()
		}

		CombineReducers {
			BindingReducer()

			Reduce<State, Action> { state, action in
				switch action {
				case let .view(viewAction):
					switch viewAction {
					case .task:
						return .run { [seriesId = state.seriesId] send in
							await send(.internal(.loadSeriesResponse(Result {
								try await series.shareable(seriesId)
							})))
						}

					case let .didUpdateDisplayScale(displayScale):
						state.displayScale = displayScale
						return .none

					case let .didUpdateColorScheme(colorScheme):
						switch colorScheme {
						case .dark: state.preferredAppearance = .dark
						case .light: state.preferredAppearance = .light
						@unknown default: state.preferredAppearance = .light
						}
						return .none
					}

				case let .internal(internalAction):
					switch internalAction {
					case let .loadSeriesResponse(.success(series)):
						state.series = series
						let scoreDomain = series.scores.scoreDomain
						state.scoreLowerBound = scoreDomain.lowerBound.roundedDown(toMultipleOf: 5)
						state.scoreUpperBound = scoreDomain.upperBound.roundedUp(toMultipleOf: 5)

						state.scoreLowerBoundRange = Array(stride(from: 0, to: state.scoreLowerBound + 1, by: 5))
						state.scoreUpperBoundRange = Array(stride(from: state.scoreUpperBound, to: Game.MAXIMUM_SCORE + 1, by: 5))
						return .none

					case let .loadSeriesResponse(.failure(error)):
						return state.errors
							.enqueue(.seriesNotFound, thrownError: error, toastMessage: Strings.Error.Toast.dataNotFound)
							.map { .internal(.errors($0)) }

					case .errors(.delegate(.doNothing)), .errors(.internal), .errors(.view):
						return .none
					}

				case .delegate, .binding:
					return .none
				}
			}
		}
		.onChange(of: \.configuration) { _, configuration in
			Reduce<State, Action> { _, _ in
				return .run { @MainActor send in
					guard let configuration else { return }
					let imageRenderer = ImageRenderer(
						content: ShareableSeriesImage(configuration: configuration)
							.frame(minWidth: 400)
							.environment(\.colorScheme, configuration.colorScheme)
					)
					imageRenderer.scale = configuration.displayScale

					guard let image = imageRenderer.uiImage else {
						return
					}

					guard !Task.isCancelled else { return }

					send(.delegate(.imageRendered(image)))
				}
				.cancellable(id: CancelID.imageRenderer, cancelInFlight: true)
			}
		}

		ErrorHandlerReducer<State, Action> { _, action in
			switch action {
			case let .internal(.loadSeriesResponse(.failure(error))):
				return error
			default:
				return nil
			}
		}
	}
}

// MARK: - View

@ViewAction(for: SeriesSharing.self)
public struct SeriesSharingView: View {
	@Bindable public var store: StoreOf<SeriesSharing>

	@Environment(\.colorScheme) var colorScheme
	@Environment(\.displayScale) var displayScale

	public init(store: StoreOf<SeriesSharing>) {
		self.store = store
	}

	public var body: some View {
		List {
			detailsSection
			domainSection
			colorSchemeSection
		}
		.task { await send(.task).finish() }
		.errors(store: store.scope(state: \.errors, action: \.internal.errors))
		.onAppear {
			send(.didUpdateColorScheme(colorScheme))
			send(.didUpdateDisplayScale(displayScale))
		}
		.onChange(of: displayScale) { send(.didUpdateDisplayScale(displayScale)) }
	}

	private var detailsSection: some View {
		Section {
			Grid(horizontalSpacing: .smallSpacing, verticalSpacing: .smallSpacing) {
				GridRow {
					ChipButton(
						icon: .calendar,
						title: Strings.Sharing.Series.Details.date,
						isOn: $store.isShowingSeriesDate.animation(.easeInOut(duration: 0.2))
					)

					ChipButton(
						icon: .listDash,
						title: Strings.Sharing.Series.Details.scoreSummary,
						isOn: $store.isShowingSeriesSummary.animation(.easeInOut(duration: 0.2))
					)
				}

				GridRow {
					ChipButton(
						icon: .personFill,
						title: Strings.Sharing.Series.Details.bowlerName,
						isOn: $store.isShowingBowlerName.animation(.easeInOut(duration: 0.2))
					)

					ChipButton(
						icon: .repeat,
						title: Strings.Sharing.Series.Details.leagueName,
						isOn: $store.isShowingLeagueName.animation(.easeInOut(duration: 0.2))
					)
				}

				GridRow {
					ChipButton(
						icon: .personFill,
						title: Strings.Sharing.Series.Details.highScore,
						isOn: $store.isLabellingHighestScore.animation(.easeInOut(duration: 0.2))
					)

					ChipButton(
						icon: .repeat,
						title: Strings.Sharing.Series.Details.lowScore,
						isOn: $store.isLabellingLowestScore.animation(.easeInOut(duration: 0.2))
					)
				}
			}
		}
		.listRowSeparator(.hidden)
		.listRowInsets(EdgeInsets())
		.listRowBackground(Color.clear)
	}

	private var domainSection: some View {
		Section(Strings.Sharing.Series.Chart.Range.title) {
			DisclosureGroup(
				content: {
					HStack {
						Picker(
							"",
							selection: $store.scoreLowerBound
						) {
							ForEach(store.scoreLowerBoundRange, id: \.self) {
								Text("\($0)")
									.tag($0)
							}
						}
						.pickerStyle(.wheel)

						Picker(
							"",
							selection: $store.scoreUpperBound
						) {
							ForEach(store.scoreUpperBoundRange, id: \.self) {
								Text("\($0)")
									.tag($0)
							}
						}
						.pickerStyle(.wheel)
					}
				},
				label: {
					Text(Strings.Sharing.Series.Chart.Range.description(store.scoreLowerBound, store.scoreUpperBound))
				}
			)
		}
		.listRowSeparator(.hidden)
	}

	private var colorSchemeSection: some View {
		Section {
			AppearancePicker(selection: $store.preferredAppearance)
		}
	}
}

#Preview {
	SeriesSharingView(
		store: .init(
			initialState: SeriesSharing.State(seriesId: UUID(0)),
			reducer: { SeriesSharing() },
			withDependencies: {
				$0[SeriesRepository.self].shareable = { @Sendable id in
					.init(
						id: id,
						date: Date(),
						bowlerName: "Joseph",
						leagueName: "Majors",
						total: 485,
						scores: [
							.init(index: 0, score: 225),
							.init(index: 1, score: 260),
						]
					)
				}
			}
		)
	)
}
