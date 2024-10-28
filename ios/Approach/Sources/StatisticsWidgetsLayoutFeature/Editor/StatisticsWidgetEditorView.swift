import ComposableArchitecture
import EquatablePackageLibrary
import ErrorsFeature
import ExtensionsPackageLibrary
import ModelsLibrary
import ModelsViewsLibrary
import ResourcePickerLibrary
import StatisticsLibrary
import StatisticsWidgetEditorFeature
import StatisticsWidgetsLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsPackageLibrary
import TipsLibrary
import ViewsLibrary

@ViewAction(for: StatisticsWidgetEditor.self)
public struct StatisticsWidgetEditorView: View {
	@Bindable public var store: StoreOf<StatisticsWidgetEditor>

	public init(store: StoreOf<StatisticsWidgetEditor>) {
		self.store = store
	}

	public var body: some View {
		StatisticsWidgetConfigurationEditorView(
			store: store.scope(state: \.editor, action: \.internal.editor),
			footer: {
				if let configuration = store.editor.configuration, let chartContent = store.widgetPreviewData {
					Section(Strings.Widget.Builder.preview) {
						Button { send(.didTapWidget) } label: {
							StatisticsWidget.Widget(configuration: configuration, chartContent: chartContent)
								.aspectRatio(2, contentMode: .fit)
						}
						.buttonStyle(TappableElement())
						.listRowInsets(EdgeInsets())
					}
				} else if store.isLoadingPreview {
					ProgressView()
				}
			}
		)
		.navigationTitle(Strings.Widget.Builder.title)
		.toolbar {
			ToolbarItem(placement: .navigationBarTrailing) {
				Button(Strings.Action.save) { send(.didTapSaveButton) }
					.disabled(!store.isSaveable)
			}
		}
		.onAppear { send(.onAppear) }
		.errors(store: store.scope(state: \.errors, action: \.internal.errors))
		.help($store.scope(state: \.destination?.help, action: \.internal.destination.help))
	}
}

extension View {
	fileprivate func help(_ store: Binding<StoreOf<StatisticsWidgetHelp>?>) -> some View {
		sheet(item: store) { (store: StoreOf<StatisticsWidgetHelp>) in
			NavigationStack {
				StatisticsWidgetHelpView(store: store)
			}
		}
	}
}
