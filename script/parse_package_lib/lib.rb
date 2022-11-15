# frozen_string_literal: true

require 'erb'
require 'yaml'
require_relative './external'
require_relative './package'
require_relative './target'

package_file = ARGV[0]
package_yaml = YAML.safe_load(File.read(package_file))

@package = Package::Package.new(
  name: package_yaml['package']['name'],
  platforms: package_yaml['package']['platforms'],
  tools_version: package_yaml['swift-tools-version']
)

@external = External.fetch_exports(package_yaml)
@packages = External.fetch_packages(package_yaml)
@targets = Target.fetch_targets(package_yaml)

@targets.each do |_, target|
  case target.type
  when 'feature'
    target.add_dependency(@external['ComposableArchitecture'])
  when 'data_provider_interface'
    target.add_dependency(@targets['SharedModelsLibrary'])
    target.add_dependency(@external['Dependencies'])
  when 'service_interface'
    target.add_dependency(@external['Dependencies'])
  end

  next if target.type == 'test'

  base_definition = package_yaml['package'][target.base].find { |t| target.name.start_with?(t['name']) }
  next if base_definition.nil? || base_definition['dependencies'].nil?

  dependencies = if base_definition['dependencies'].is_a?(Array)
                   base_definition['dependencies']
                 elsif target.interface?
                   base_definition['dependencies']['interface']
                 else
                   base_definition['dependencies']['implementation']
                 end

  next if dependencies.nil?

  dependencies.each do |d|
    dep = @targets[d] || @external[d]
    target.add_dependency(dep.interface || dep)
  end
end

def find_transient_dependencies(target, skip: true)
  target.dependencies.flat_map do |d|
    if @external.key?(d)
      next skip ? [] : [d]
    end

    dep = @targets[d]
    if dep.nil?
      next skip ? [] : [d]
    end

    find_transient_dependencies(dep, skip: false) + (skip ? [] : [d])
  end
end

@targets.each do |_, target|
  next if target.type == 'test'

  transient = find_transient_dependencies(target)
  transient.each do |d|
    dep = @targets[d] || @external[d]
    next if dep.nil?

    dep = dep.interface || dep
    target.remove_dependency(dep)
    # raise "#{target.name} has unnecessary transient dependency #{dep.name}" if target.dependencies_include?(dep)
  end
end

@collections = [
  { targets: @targets.select { |_, t| t.base == 'feature' }, name: 'Features' },
  { targets: @targets.select { |_, t| t.base == 'data_provider' }, name: 'Data Providers' },
  { targets: @targets.select { |_, t| t.base == 'service' }, name: 'Services' },
  { targets: @targets.select { |_, t| t.base == 'library' }, name: 'Libraries' }
]

output = ERB.new(File.read(File.join(File.dirname(__FILE__), './package-template.erb')))
puts output.result(binding)
